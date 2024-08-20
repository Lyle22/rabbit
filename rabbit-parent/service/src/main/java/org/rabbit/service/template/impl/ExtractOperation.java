package org.rabbit.service.template.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.rabbit.entity.template.DocumentTemplateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

/**
 * Extract operation for generate document
 *
 * @author nine rabbit
 */
@Slf4j
@Service
public class ExtractOperation {

    public static Set<String> EXCEL_EXTENSION = Stream.of("xlsx", "xlsm", "xls", "xltx", "xltm", "xlam").collect(Collectors.toSet());

    public static Set<String> PDF_EXTENSION = Stream.of("pdf").collect(Collectors.toSet());

    public static Set<String> PPT_EXTENSION = Stream.of("pptx", "ppt", "pptm", "pps", "ppsx", "pot", "potx", "thmx").collect(Collectors.toSet());

    public static Set<String> WORD_EXTENSION = Stream.of("doc", "docx", "docm", "dot", "dotx").collect(Collectors.toSet());

    /**
     * Use poi to get the file itself and traverse it to get the content to be filled in
     * (select the corresponding poi according to the file format) and get the content of {}
     */
    public List<String> execute(DocumentTemplateRequestDTO requestDTO) throws IOException {
        return execute(requestDTO.getFile());
    }

    public List<String> execute(MultipartFile file) throws IOException {
        return execute(file.getBytes(), file.getOriginalFilename());
    }

    public List<String> execute(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return execute(fileContent, file.getName());
    }

    public List<String> execute(byte[] content, String fileName) {
        try {
            List<String> keys = Lists.newArrayList();
            String extension = FilenameUtils.getExtension(fileName);
            ByteArrayInputStream basis = new ByteArrayInputStream(content);
            if ("doc".equals(extension)) {
                keys = extractDocFileVariables(new HWPFDocument(basis));
            } else if ("docx".equals(extension)) {
                keys = extractDocxFileVariables(basis);
            } else if ("rtf".equals(extension)) {
                keys = extractRtfFileVariables(basis);
            } else if ("pptx".equals(extension)) {
                keys = extractPPTXVariables(new XMLSlideShow(basis));
            } else if ("ppt".equals(extension)) {
                keys = extractPPTTVariables(new HSLFSlideShow(basis));
            } else if (EXCEL_EXTENSION.contains(extension)) {
                keys = extractExcelVariables(new XSSFWorkbook(basis));
            } else {
                return keys;
            }
            return keys.stream().distinct().collect(Collectors.toList());
        } catch (IOException e) {
            log.error("error message {}", e.getMessage());
        }
        return Lists.newArrayList();
    }

    /**
     * Replaces the specified text in the contents of File
     *
     * @param docFilePath the file path of local
     * @param variables   all variables value
     * @param extension   the file extension
     */
    public byte[] replace(String docFilePath, String extension, Map<String, String> variables) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(); FileInputStream fis = new FileInputStream(docFilePath)) {
            if ("doc".equals(extension)) {
                HWPFDocument doc = new HWPFDocument(fis);
                HWPFDocument hwpfDocument = replace(variables, doc);
                hwpfDocument.write(os);
            } else if ("docx".equals(extension)) {
                XWPFDocument doc = replace(variables, new XWPFDocument(fis));
                doc.write(os);
            } else if ("pptx".equals(extension)) {
                XMLSlideShow slideShow = new XMLSlideShow(fis);
                XMLSlideShow newPPTX = replace(variables, slideShow);
                newPPTX.write(os);
            } else if ("ppt".equals(extension)) {
                HSLFSlideShow slideShow = new HSLFSlideShow(fis);
                HSLFSlideShow newPPT = replace(variables, slideShow);
                newPPT.write(os);
            } else if (EXCEL_EXTENSION.contains(extension)) {
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                Workbook doc = replace(variables, workbook, null);
                doc.write(os);
            } else {
                return fis.readAllBytes();
            }
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Replace specified fields in the read word template content
     *
     * @param doc the document file
     * @return List<String>
     */
    private List<String> extractDocFileVariables(HWPFDocument doc) {
        // 替换读取到的word模板内容的指定字段
        Range range = doc.getRange();
        String value = range.text();
        if (!StringUtils.isBlank(value) && value.contains("${")) {
            return getDOCValue(value, "\\$\\{", "\\}", 2, 1);
        }
        return new ArrayList<>();
    }

    private List<String> extractDocxFileVariables(ByteArrayInputStream is) {
        String value = "";
        try {
            if (FileMagic.valueOf(is) == FileMagic.OLE2) {
                WordExtractor ex = new WordExtractor(is);
                value = ex.getText();
                ex.close();
            } else if (FileMagic.valueOf(is) == FileMagic.OOXML) {
                XWPFDocument doc = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                value = extractor.getText();
                extractor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isBlank(value) && value.contains("${")) {
            return getDOCValue(value, "\\$\\{", "\\}", 2, 1);
        }
        return new ArrayList<>();
    }

    private List<String> extractRtfFileVariables(InputStream in) {
        String value = "";
        try {
            RTFEditorKit rtf = new RTFEditorKit();
            DefaultStyledDocument styledDoc = new DefaultStyledDocument();
            rtf.read(in, styledDoc, 0);
            value = new String(styledDoc.getText(0, styledDoc.getLength()).getBytes("ISO8859_1"));
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isBlank(value) && value.contains("${")) {
            return getDOCValue(value, "\\$\\{", "\\}", 2, 1);
        }
        return new ArrayList<>();
    }

    private static List<String> getDOCValue(String msg, String begin, String end, int beginSize, int endSize) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(begin + "(.*?)" + end);
        Matcher m = p.matcher(msg);
        while (m.find()) {
            String group = m.group();
            list.add(group.substring(beginSize, m.group().length() - endSize));
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    private List<String> extractPPTXVariables(XMLSlideShow ppt) {
        List<String> params = new ArrayList<>();
        try {
            for (XSLFSlide slide : ppt.getSlides()) {
                //获取每张的shape
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String value = textShape.getText();
                        //文字替换
                        if (!StringUtils.isBlank(value) && value.contains("${")) {
                            params.addAll(getDOCValue(value, "\\$\\{", "\\}", 2, 1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    private List<String> extractPPTTVariables(HSLFSlideShow ppt) {
        List<String> params = new ArrayList<>();
        for (HSLFSlide slide : ppt.getSlides()) {
            //获取每张的shape
            for (HSLFShape shape : slide.getShapes()) {
                if (shape instanceof HSLFTextShape) {
                    HSLFTextShape textShape = (HSLFTextShape) shape;
                    String value = textShape.getText();
                    //文字替换
                    if (!StringUtils.isBlank(value) && value.contains("${")) {
                        params.addAll(getDOCValue(value, "\\$\\{", "\\}", 2, 1));
                    }
                }
            }
        }
        return params;
    }

    private List<String> extractExcelVariables(XSSFWorkbook workbook) {
        List<String> params = new ArrayList<>();
        Sheet sheet = null;
        for (int a = 0; a < workbook.getNumberOfSheets(); a++) {
            sheet = workbook.getSheetAt(a);
            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                if (row == null) {
                    continue;
                }
                int num = row.getLastCellNum();
                for (int i = 0; i < num; i++) {
                    Cell cell = row.getCell(i);
                    String value = convertCellValueToString(cell);
                    if (cell == null || cell.getCellType() == NUMERIC || StringUtils.isBlank(value)) {
                        continue;
                    }
                    if (value.contains("${")) {
                        List<String> docValue = getDOCValue(value, "\\$\\{", "\\}", 2, 1);
                        params.addAll(docValue);
                    }
                }
            }
        }
        return params;
    }

    private XWPFDocument replaceText(XWPFDocument doc, String originalText, String updatedText) {
        replaceTextInParagraphs(doc.getParagraphs(), originalText, updatedText);
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    replaceTextInParagraphs(cell.getParagraphs(), originalText, updatedText);
                }
            }
        }
        return doc;
    }

    private void replaceTextInParagraphs(List<XWPFParagraph> paragraphs, String originalText, String updatedText) {
        paragraphs.forEach(paragraph -> replaceTextInParagraph(paragraph, originalText, updatedText));
    }

    private void replaceTextInParagraph(XWPFParagraph paragraph, String originalText, String updatedText) {
        List<XWPFRun> runs = paragraph.getRuns();
        // 文本合并逻辑： XWPFRun获取文本时，${key}可能会被word拆分成了几段，导致无法识别变量
        for (int i = 0; i < runs.size(); i++) {
            String text0 = runs.get(i).getText(runs.get(i).getTextPosition());
            if (text0 != null && text0.contains("$")) {
                int startIndex = text0.lastIndexOf("$");
                int endIndex = 1;
                if (startIndex != -1) {
                    endIndex = text0.substring(startIndex).indexOf("}");
                }
                if (endIndex < 0) {
                    // 记录分隔符中间跨越的runs数量，用于字符串拼接和替换
                    int num = 0;
                    int j = i + 1;
                    for (; j < runs.size(); j++) {
                        String text1 = runs.get(j).getText(runs.get(j).getTextPosition());
                        if (text1 != null && text1.contains("}")) {
                            num = j - i;
                            break;
                        }
                    }
                    if (num != 0) {
                        // num!=0说明找到了@@配对，需要替换
                        StringBuilder newText = new StringBuilder();
                        for (int s = i; s <= i + num; s++) {
                            String text2 = runs.get(s).getText(runs.get(s).getTextPosition());
                            String replaceText = text2;
                            if (s == i && text2.contains("$") && text2.contains("}")) {
                                newText.append(text2);
                            } else if (s == i && text2.contains("$")) {
                                replaceText = text2.substring(0, text2.indexOf("$"));
                                newText.append(text2.substring(text2.indexOf("$")));
                            } else if (text2.contains("}")) {
                                replaceText = text2.substring(replaceText.indexOf("}") + 1);
                                newText.append(text2.substring(0, text2.indexOf("}") + 1));
                            } else {
                                replaceText = "";
                                newText.append(text2);
                            }
                            runs.get(s).setText(replaceText, 0);
                        }
                        runs.get(i).setText(newText.toString(), 0);
                        i = i - 1;
                    }
                }
            }
        }

        // Execute replace text
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null && text.contains(originalText)) {
                String updatedRunText = text.replace(originalText, updatedText);
                run.setText(updatedRunText, 0);
            }
        }
    }

    /**
     * 替换读取到的word模板内容的指定字段
     *
     * @param variables all variables value
     * @param doc       the read document
     */
    private XWPFDocument replace(Map<String, String> variables, XWPFDocument doc) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String key = String.format("${%s}", entry.getKey());
            doc = replaceText(doc, key, entry.getValue());
        }
        return doc;
    }

    /**
     * 替换读取到的word模板内容的指定字段
     *
     * @param variables all variables value
     * @param doc       the read document
     */
    private HWPFDocument replace(Map<String, String> variables, HWPFDocument doc) {
        // 替换读取到的word模板内容的指定字段
        Range range = doc.getRange();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            // key wraps ${} in paramMap for replace processing
            String key = String.format("${%s}", entry.getKey());
            range.replaceText(key, entry.getValue());
        }
        return doc;
    }

    private XMLSlideShow replace(Map<String, String> variables, XMLSlideShow ppt) {
        try {
            for (XSLFSlide slide : ppt.getSlides()) {
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String value = textShape.getText();
                        if (!StringUtils.isBlank(value) && value.contains("${")) {
                            textShape.setText(replaceParamValue(value, variables));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ppt;
    }

    private static String replaceParamValue(String value, Map<String, String> replaceMap) {
        String replaceValue = value;
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            String key = String.format("\\$\\{%s\\}", entry.getKey());
            String value1 = entry.getValue();
            replaceValue = replaceValue.replaceAll(key, value1);
        }
        return replaceValue;
    }

    private HSLFSlideShow replace(Map<String, String> replaceMap, HSLFSlideShow ppt) {
        try {
            for (HSLFSlide slide : ppt.getSlides()) {
                for (HSLFShape shape : slide.getShapes()) {
                    if (shape instanceof HSLFTextShape) {
                        HSLFTextShape textShape = (HSLFTextShape) shape;
                        String value = textShape.getText();
                        if (!StringUtils.isBlank(value) && value.contains("${")) {
                            textShape.setText(replaceParamValue(value, replaceMap));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ppt;
    }

    private Workbook replace(Map<String, String> replaceMap, Workbook workbook, Integer endRowIndex) {
        try {
            Sheet sheet = null;
            for (int a = 0; a < workbook.getNumberOfSheets(); a++) {
                sheet = workbook.getSheetAt(a);
                Iterator<Row> rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    Row row = rows.next();
                    if (row == null) {
                        continue;
                    }
                    int num = row.getLastCellNum();
                    if (endRowIndex != null && row.getRowNum() > endRowIndex) {
                        break;
                    }
                    for (int i = 0; i < num; i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null || cell.getCellType() == NUMERIC || cell.getStringCellValue() == null) {
                            continue;
                        }
                        String value = cell.getStringCellValue();
                        if (!StringUtils.isBlank(value) && value.contains("${")) {
                            cell.setCellValue(replaceParamValue(value, replaceMap));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public static String convertCellValueToString(Cell cell) {
        if (cell == null) {
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:
                Double doubleValue = cell.getNumericCellValue();
                DecimalFormat df = new DecimalFormat("0");
                returnValue = df.format(doubleValue);
                break;
            case STRING:
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case BLANK:
                break;
            case FORMULA:
                returnValue = cell.getCellFormula();
                break;
            default:
                log.info("Cell type is match for all");
                break;
        }
        return returnValue;
    }
}
