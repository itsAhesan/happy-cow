package com.xworkz.happycow.util;


import com.xworkz.happycow.dto.ProductDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelHelper {

    // Write list of ProductDTO to OutputStream as .xlsx workbook
    public void productsToExcel(List<ProductDTO> products, OutputStream os) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Product Name");
        header.createCell(1).setCellValue("Product Price");
        header.createCell(2).setCellValue("Product Type");
        header.createCell(3).setCellValue("Active");
        header.createCell(4).setCellValue("Created By");
        header.createCell(5).setCellValue("Created At");

        // Data rows
        int rowIdx = 1;
        for (ProductDTO p : products) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(defaultString(p.getProductName()));
            if (p.getProductPrice() != null) r.createCell(1).setCellValue(p.getProductPrice());
            r.createCell(2).setCellValue(defaultString(p.getProductType()));
            r.createCell(3).setCellValue(p.getActive() == null ? "true" : p.getActive().toString());
            r.createCell(4).setCellValue(defaultString(p.getCreatedBy()));
            if (p.getCreatedAt() != null) r.createCell(5).setCellValue(p.getCreatedAt().toString());
        }

        // Autosize columns
        for (int i = 0; i <= 5; i++) sheet.autoSizeColumn(i);

        workbook.write(os);
        workbook.close();
    }

    // Read an Excel InputStream and convert valid rows to ProductDTO
    public List<ProductDTO> excelToProducts(InputStream is) throws IOException {
        List<ProductDTO> products = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(is); // auto-detects .xls or .xlsx
        Sheet sheet = workbook.getSheetAt(0);

        if (sheet == null) {
            workbook.close();
            return products;
        }

        // Determine starting row (assume header at row 0)
        int firstRow = sheet.getFirstRowNum() + 1;
        int lastRow = sheet.getLastRowNum();

        for (int r = firstRow; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String name = getCellString(row.getCell(0));
            Double price = getCellDouble(row.getCell(1));
            String type = getCellString(row.getCell(2));

            // Basic validation: require name and price
            if (name == null || name.isEmpty() || price == null) {
                // skip invalid row, caller will get size & skipped count
                continue;
            }

            ProductDTO dto = new ProductDTO();
            dto.setProductName(name.trim());
            dto.setProductPrice(price);
            dto.setProductType(type == null ? "Sell" : type.trim());
            products.add(dto);
        }

        workbook.close();
        return products;
    }

    private String defaultString(String s) {
        return s == null ? "" : s;
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());
        if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
        if (cell.getCellType() == CellType.FORMULA) {
            try { return cell.getStringCellValue(); } catch (Exception e) { return String.valueOf(cell.getNumericCellValue()); }
        }
        return null;
    }

    private Double getCellDouble(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try { return Double.parseDouble(cell.getStringCellValue().trim()); } catch (NumberFormatException e) { return null; }
        } else if (cell.getCellType() == CellType.FORMULA) {
            try { return cell.getNumericCellValue(); } catch (Exception e) { return null; }
        }
        return null;
    }


}
