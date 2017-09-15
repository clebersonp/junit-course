package br.com.cleberson.teste.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;

public class TesteExcel {

	public static void main(String[] args) throws IOException {
		String caminho = "D:\\Downloads\\201709_cleberson_pauluci.xlsm";
		lerExcel(caminho);
	}

	public static synchronized void lerExcel(String caminho) throws FileNotFoundException, IOException {
		FileInputStream inputStream = new FileInputStream(caminho);
		XSSFWorkbook wb = new XSSFWorkbook(inputStream);
		wb.setWorkbookType(XSSFWorkbookType.XLSM);
		
		XSSFSheet sheet = wb.getSheet("PLANILHA DE HORAS");
		
		String matriculaColaborador = sheet.getRow(5).getCell(CellReference.convertColStringToIndex("H")).getStringCellValue();
		BigDecimal totalHoras = new BigDecimal(sheet.getRow(41).getCell(CellReference.convertColStringToIndex("L")).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
		
		System.err.println("MatriculaColaborador: " + matriculaColaborador);
		System.err.println("Total de Horas: " + totalHoras);
		
		wb.close();
		inputStream.close();
	}
	
}
