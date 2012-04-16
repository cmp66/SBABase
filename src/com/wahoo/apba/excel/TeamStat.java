/*
 * TeamStat.java
 *
 * Created on January 16, 2004, 4:26 PM
 */


package com.wahoo.apba.excel;



import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Alignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.jdom.Element;

import com.wahoo.apba.database.SeriesStatRecord;
import com.wahoo.apba.resourcemanagers.StatsManager;
import com.wahoo.util.Email;

@SuppressWarnings("deprecation")

/**
 *
 * @author  cphillips
 */
public class TeamStat
{

    private StatsManager statsManager = null;
    private static HashMap<String, String> _seriesExportFiles = new HashMap<String, String>(100);;

    private String exportFilesDir = null;
    private String importFilesDir = null;
    
    /** Creates a new instance of TeamStat */
    public TeamStat ()
    {
       // _seriesExportFiles = new HashMap(100);
        //createSeriesExportFileList();
        
    }
    
    public void init()
    {
    	createSeriesExportFileList();
    }
    
    public void setStatsManager(StatsManager inMgr)
    {
    	this.statsManager = inMgr;
    }
    
    public void setExportFilesDir(String inVal)
    {
    	this.exportFilesDir = inVal;
    }
    
    public void setImportFilesDir(String inVal)
    {
    	this.importFilesDir = inVal;
    }
    
    private void createSeriesExportFileList()
    {
        //File wkDir = new File(WebProperties.getWebProperties().get("ExportFilesDir") + "/series");
    	File wkDir = new File(exportFilesDir + "/series");
        String[] wkFiles = wkDir.list();
        
        for (int i = 0; i < wkFiles.length; i++)
        {
            _seriesExportFiles.put(wkFiles[i], "SERIES");
        }
    }
    
    public boolean checkForExportSeriesFile(String inFileName)
    {
        if (_seriesExportFiles.containsKey(inFileName))
            return true;
            
        return false;
    }
    
    public void processImportStatsFiles()
    {
        File wkDir = new File(importFilesDir + "/series");
        File[] wkFiles = wkDir.listFiles();
        
        for (int i = 0; i < wkFiles.length; i++)
        {
            File wkFile = wkFiles[i];
            processSeriesStatFile(wkFile);
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    public void createTeamStatSheet(int inYear, int inTeam, String inFileName)
    {
        Element wkTeamStats = statsManager.getTeamStats(inYear, inTeam);
        Element wkBattingStats = wkTeamStats.getChild("battingstats");
        Element wkPitchingStats = wkTeamStats.getChild("pitchingstats");
        
        int  wkRow = 2;
        
        try
        {
            String wkFilename = exportFilesDir + "/season/" + inFileName + ".xls";
            System.out.println("START EXCEL");
            WritableWorkbook wkWorkbook = Workbook.createWorkbook(new File(wkFilename));
            WritableSheet wkSheet = wkWorkbook.createSheet("Team Stats", 0);
        
            Iterator wkIter = wkBattingStats.getChildren().iterator();
            if (wkIter.hasNext())
            {
                Element wkPlayer = (Element) wkIter.next();
                System.out.println("START BATTING LABEL");
                createBattingLabelRow(wkSheet, wkPlayer, wkRow++);
                System.out.println("END BATTING LABEL");
                createBattingRow(wkSheet, wkPlayer, wkRow++);
                while (wkIter.hasNext())
                {
                    wkPlayer = (Element) wkIter.next();
                    createBattingRow(wkSheet, wkPlayer,wkRow++);
                }
                
                wkRow += 3;
                
            }

            wkRow += 3;
            wkIter = wkPitchingStats.getChildren().iterator();
            if (wkIter.hasNext())
            {
                Element wkPlayer = (Element) wkIter.next();
                System.out.println("START PITCHING LABEL");
                createPitchingLabelRow(wkSheet, wkPlayer, wkRow++);
                System.out.println("END PITCHING LABEL");
                createPitchingRow(wkSheet, wkPlayer, wkRow++);
                while (wkIter.hasNext())
                {
                    wkPlayer = (Element) wkIter.next();
                    createPitchingRow(wkSheet, wkPlayer,wkRow++);
                }
                
                wkRow += 3;
                
            }            
            wkWorkbook.write();
            wkWorkbook.close();
            System.out.println("EXCEL DONE");
            
        }
        catch (IOException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }        
    }

    @SuppressWarnings("unchecked")
    public void createTeamSeriesStatSheet(int inYear, int inSeries, int inReportingTeam, int inOtherTeam)
    {
        Element wkTeamSeriesStats = statsManager.getTeamSeriesStats(inYear, inSeries, inReportingTeam, inOtherTeam);
        //String wkReportingTeamName = wkTeamSeriesStats.getChild("ReportName").getText();
        //String wkOtherTeamName = wkTeamSeriesStats.getChild("OtherName").getText();
        
        
        Element wkTeamStats = wkTeamSeriesStats.getChild("ReportingTeamStats");
        Element wkBattingStats = wkTeamStats.getChild("battingstats");
        Element wkPitchingStats = wkTeamStats.getChild("pitchingstats");
        
        //boolean wkGetOtherTeam = java.lang.Boolean.getBoolean("EnterOtherStats");
        boolean wkGetOtherTeam = false;
        
        int  wkRow = 2;
        
        try
        {
            String wkFilename = exportFilesDir + "/series/" + inYear + "-" + inSeries + "-" + inReportingTeam + ".xls";
            System.out.println("START EXCEL");
            WritableWorkbook wkWorkbook = Workbook.createWorkbook(new File(wkFilename));
            WritableSheet wkSheet = wkWorkbook.createSheet("Team Stats", 0);
            
            createSeriesLabelRow(wkSheet, wkRow++);
            createSeriesRow(wkSheet, wkRow++, inYear, inSeries, inReportingTeam, inOtherTeam);
            
            wkRow++;
            
        
            Iterator wkIter = wkBattingStats.getChildren().iterator();
            if (wkIter.hasNext())
            {
                Element wkPlayer = (Element) wkIter.next();
                System.out.println("START BATTING LABEL");
                createBattingLabelRow(wkSheet, wkPlayer, wkRow++);
                System.out.println("END BATTING LABEL");
                createBattingRow(wkSheet, wkPlayer, wkRow++);
                while (wkIter.hasNext())
                {
                    wkPlayer = (Element) wkIter.next();
                    createBattingRow(wkSheet, wkPlayer,wkRow++);
                }
                
                wkRow += 3;
                
            }

            wkRow += 3;
            wkIter = wkPitchingStats.getChildren().iterator();
            if (wkIter.hasNext())
            {
                Element wkPlayer = (Element) wkIter.next();
                System.out.println("START PITCHING LABEL");
                createPitchingLabelRow(wkSheet, wkPlayer, wkRow++);
                System.out.println("END PITCHING LABEL");
                createPitchingRow(wkSheet, wkPlayer, wkRow++);
                while (wkIter.hasNext())
                {
                    wkPlayer = (Element) wkIter.next();
                    createPitchingRow(wkSheet, wkPlayer,wkRow++);
                }
                
                wkRow += 3;
                
            }      
            
            if (wkGetOtherTeam)      
            {
                wkTeamStats = wkTeamSeriesStats.getChild("OtherTeamStats");
                wkBattingStats = wkTeamStats.getChild("battingstats");
                wkPitchingStats = wkTeamStats.getChild("pitchingstats");
            
                wkIter = wkBattingStats.getChildren().iterator();
                if (wkIter.hasNext())
                {
                    Element wkPlayer = (Element) wkIter.next();
                    System.out.println("START OTHER BATTING LABEL");
                    createOtherBattingLabelRow(wkSheet, wkPlayer, wkRow++);
                    System.out.println("END OTHER BATTING LABEL");
                    createOtherBattingRow(wkSheet, wkPlayer, wkRow++);
                    while (wkIter.hasNext())
                    {
                        wkPlayer = (Element) wkIter.next();
                        createOtherBattingRow(wkSheet, wkPlayer,wkRow++);
                    }
                
                    wkRow += 3;
                
                }

                wkRow += 3;
                wkIter = wkPitchingStats.getChildren().iterator();
                if (wkIter.hasNext())
                {
                    Element wkPlayer = (Element) wkIter.next();
                    System.out.println("START OTHER PITCHING LABEL");
                    createOtherPitchingLabelRow(wkSheet, wkPlayer, wkRow++);
                    System.out.println("END OTHER PITCHING LABEL");
                    createOtherPitchingRow(wkSheet, wkPlayer, wkRow++);
                    while (wkIter.hasNext())
                    {
                        wkPlayer = (Element) wkIter.next();
                        createOtherPitchingRow(wkSheet, wkPlayer,wkRow++);
                    }
                
                    wkRow += 3;
                
                } 
            }
            wkWorkbook.write();
            wkWorkbook.close();
            System.out.println("EXCEL DONE");
            _seriesExportFiles.put(inYear + "-" + inSeries + "-" + inReportingTeam + ".xls", "SERIES");
            
        }
        catch (IOException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }        
    }
            
    private void createOtherBattingLabelRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            int i = 1;
            WritableFont wkBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
            WritableCellFormat wkBoldFormat = new WritableCellFormat (wkBold);
            wkBoldFormat.setAlignment(Alignment.CENTRE);
            WritableCell wkCell = null;
            
            
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("name").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("id").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("games").getAttributeValue("DisplayName")));

            for (int j=1 ; j < i ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkBoldFormat);
            }
            

            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }

    private void createBattingLabelRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            int i = 1;
            WritableFont wkBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
            WritableCellFormat wkBoldFormat = new WritableCellFormat (wkBold);
            wkBoldFormat.setAlignment(Alignment.CENTRE);
            WritableCell wkCell = null;
            
            
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("name").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("id").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("games").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_ab").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_runs").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_hits").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_rbi").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_doubles").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_triples").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_hr").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_walks").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_strikeouts").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_sb").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_cs").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("bat_hbp").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("errors").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("avg").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("slug").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("obp").getAttributeValue("DisplayName")));

            for (int j=1 ; j < i ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkBoldFormat);
            }
            

            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
        
    private void createSeriesLabelRow(WritableSheet inSheet, int inRow)
    {
        try
        {
            int i = 1;
            WritableFont wkBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
            WritableCellFormat wkBoldFormat = new WritableCellFormat (wkBold);
            wkBoldFormat.setAlignment(Alignment.CENTRE);
            WritableCell wkCell = null;
            
            
            inSheet.addCell(new Label(i++, inRow, "Year"));
            inSheet.addCell(new Label(i++, inRow, "Series"));
            inSheet.addCell(new Label(i++, inRow, "R-Team"));
            inSheet.addCell(new Label(i++, inRow, "O-Team"));

            for (int j=1 ; j < i ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkBoldFormat);
            }
            

            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
    
    
    private void createBattingRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            NumberFormat wkNumberFormat = new NumberFormat(".000");
            StringBuffer wkFormulaString = new StringBuffer(128);
            WritableCellFormat wkNumCellFormat = new WritableCellFormat(wkNumberFormat);
            WritableCellFormat wkIntegerFormat = new WritableCellFormat(NumberFormats.INTEGER);
            wkNumCellFormat.setAlignment(Alignment.RIGHT);
            wkIntegerFormat.setAlignment(Alignment.RIGHT);
            WritableCell wkCell = null;

            
            inSheet.addCell(new Label(1, inRow, inPlayer.getChild("name").getText()));
            inSheet.addCell(new Label(2, inRow, inPlayer.getChild("id").getText()));
            inSheet.addCell(new Label(3, inRow, inPlayer.getChild("games").getText()));
            inSheet.addCell(new Label(4, inRow, inPlayer.getChild("bat_ab").getText()));
            inSheet.addCell(new Label(5, inRow, inPlayer.getChild("bat_runs").getText()));
            inSheet.addCell(new Label(6, inRow, inPlayer.getChild("bat_hits").getText()));
            inSheet.addCell(new Label(7, inRow, inPlayer.getChild("bat_rbi").getText()));
            inSheet.addCell(new Label(8, inRow, inPlayer.getChild("bat_doubles").getText()));
            inSheet.addCell(new Label(9, inRow, inPlayer.getChild("bat_triples").getText()));
            inSheet.addCell(new Label(10, inRow, inPlayer.getChild("bat_hr").getText()));
            inSheet.addCell(new Label(11, inRow, inPlayer.getChild("bat_walks").getText()));
            inSheet.addCell(new Label(12, inRow, inPlayer.getChild("bat_strikeouts").getText()));
            inSheet.addCell(new Label(13, inRow, inPlayer.getChild("bat_sb").getText()));
            inSheet.addCell(new Label(14, inRow, inPlayer.getChild("bat_cs").getText()));
            inSheet.addCell(new Label(15, inRow, inPlayer.getChild("bat_hbp").getText()));
            inSheet.addCell(new Label(16, inRow, inPlayer.getChild("errors").getText()));
            
            wkFormulaString.append(CellReferenceHelper.getCellReference(6, inRow)).append("/");
            wkFormulaString.append(CellReferenceHelper.getCellReference(4, inRow));
            inSheet.addCell(new Formula(17, inRow, wkFormulaString.toString()));
            wkCell = inSheet.getWritableCell(17, inRow);
            wkCell.setCellFormat(wkNumCellFormat);
            
            wkFormulaString = new StringBuffer(128);
            wkFormulaString.append("(").append(CellReferenceHelper.getCellReference(6, inRow)).append("+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(8, inRow)).append("*1+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(9, inRow)).append("*2+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(10, inRow)).append("*3)/");
            wkFormulaString.append(CellReferenceHelper.getCellReference(4, inRow));
            inSheet.addCell(new Formula(18, inRow, wkFormulaString.toString()));
            wkCell = inSheet.getWritableCell(18, inRow);
            wkCell.setCellFormat(wkNumCellFormat);
            
            wkFormulaString = new StringBuffer(128);
            wkFormulaString.append("(").append(CellReferenceHelper.getCellReference(6, inRow)).append("+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(11, inRow)).append("+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(15, inRow)).append(")/(");
            wkFormulaString.append(CellReferenceHelper.getCellReference(4, inRow)).append("+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(11, inRow)).append("+");
            wkFormulaString.append(CellReferenceHelper.getCellReference(15, inRow)).append(")");
            inSheet.addCell(new Formula(19, inRow, wkFormulaString.toString()));
            wkCell = inSheet.getWritableCell(19, inRow);
            wkCell.setCellFormat(wkNumCellFormat);
            
            
            for (int j=1 ; j < 17 ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkIntegerFormat);
            }
            


            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }

    private void createOtherBattingRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            int i = 1;
            NumberFormat wkNumberFormat = new NumberFormat(".###");
            WritableCellFormat wkNumCellFormat = new WritableCellFormat(wkNumberFormat);
            wkNumCellFormat.setAlignment(Alignment.RIGHT);
            WritableCell wkCell = null;

            
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("name").getText()));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("id").getText()));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("games").getText()));
            
            for (int j=1 ; j < i ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkNumCellFormat);
            }
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
        
    private void createSeriesRow(WritableSheet inSheet, int inRow, int inYear, int inSeries, int inReportTeam, int inOtherTeam)
    {
        try
        {
            int i = 1;
            
            inSheet.addCell(new Label(i++, inRow, inYear+""));
            inSheet.addCell(new Label(i++, inRow, inSeries+""));
            inSheet.addCell(new Label(i++, inRow, inReportTeam+""));
            inSheet.addCell(new Label(i++, inRow, inOtherTeam+""));
            
            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
    
    
    private void createPitchingLabelRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            int i = 1;
            WritableFont wkBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
            WritableCellFormat wkBoldFormat = new WritableCellFormat (wkBold);
            wkBoldFormat.setAlignment(Alignment.CENTRE);
            WritableCell wkCell = null;
            
            
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("name").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("id").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_gp").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_gs").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_cg").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_sho").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_wins").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_loss").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_save").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_ipfull").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_ipfract").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_hits").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_runs").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_er").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_walks").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_strikeouts").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_hr").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("era").getAttributeValue("DisplayName")));

            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
            for (int j=1 ; j < i ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkBoldFormat);
            }
            
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
    
    private void createOtherPitchingLabelRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            int i = 1;
            WritableFont wkBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
            WritableCellFormat wkBoldFormat = new WritableCellFormat (wkBold);
            wkBoldFormat.setAlignment(Alignment.CENTRE);
            WritableCell wkCell = null;
            
            
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("name").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("id").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_gs").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_ipfull").getAttributeValue("DisplayName")));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_ipfract").getAttributeValue("DisplayName")));
           
            for (int j=1 ; j < i ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkBoldFormat);
            }
            
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
    
    private void createPitchingRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            StringBuffer wkFormulaString = new StringBuffer(128);
            NumberFormat wkNumberFormat = new NumberFormat("#.00");
            WritableCellFormat wkNumCellFormat = new WritableCellFormat(wkNumberFormat);
            wkNumCellFormat.setAlignment(Alignment.RIGHT);
            WritableCellFormat wkIntegerFormat = new WritableCellFormat(NumberFormats.INTEGER);
            wkIntegerFormat.setAlignment(Alignment.RIGHT);
            WritableCell wkCell = null;

            inSheet.addCell(new Label(1, inRow, inPlayer.getChild("name").getText()));
            inSheet.addCell(new Label(2, inRow, inPlayer.getChild("id").getText()));
            inSheet.addCell(new Label(3, inRow, inPlayer.getChild("pitch_gp").getText()));
            inSheet.addCell(new Label(4, inRow, inPlayer.getChild("pitch_gs").getText()));
            inSheet.addCell(new Label(5, inRow, inPlayer.getChild("pitch_cg").getText()));
            inSheet.addCell(new Label(6, inRow, inPlayer.getChild("pitch_sho").getText()));
            inSheet.addCell(new Label(7, inRow, inPlayer.getChild("pitch_wins").getText()));
            inSheet.addCell(new Label(8, inRow, inPlayer.getChild("pitch_loss").getText()));
            inSheet.addCell(new Label(9, inRow, inPlayer.getChild("pitch_save").getText()));
            inSheet.addCell(new Label(10, inRow, inPlayer.getChild("pitch_ipfull").getText()));
            inSheet.addCell(new Label(11, inRow, inPlayer.getChild("pitch_ipfract").getText()));
            inSheet.addCell(new Label(12, inRow, inPlayer.getChild("pitch_hits").getText()));
            inSheet.addCell(new Label(13, inRow, inPlayer.getChild("pitch_runs").getText()));
            inSheet.addCell(new Label(14, inRow, inPlayer.getChild("pitch_er").getText()));
            inSheet.addCell(new Label(15, inRow, inPlayer.getChild("pitch_walks").getText()));
            inSheet.addCell(new Label(16, inRow, inPlayer.getChild("pitch_strikeouts").getText()));
            inSheet.addCell(new Label(17, inRow, inPlayer.getChild("pitch_hr").getText()));
            
            wkFormulaString.append("(").append(CellReferenceHelper.getCellReference(14, inRow)).append("*9)/(");
            wkFormulaString.append(CellReferenceHelper.getCellReference(10, inRow)).append("+ 0.33*").append(CellReferenceHelper.getCellReference(11, inRow)).append(")");
            inSheet.addCell(new Formula(18, inRow, wkFormulaString.toString()));
            //inSheet.addCell(new jxl.write.Number(18, inRow, Double.parseDouble(inPlayer.getChild("era").getText())));
            wkCell = inSheet.getWritableCell(18, inRow);
            wkCell.setCellFormat(wkNumCellFormat);
          
            for (int j=1 ; j < 18 ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkIntegerFormat);
            }
            


            //jxl.write.Number number = new jxl.write.Number(3, 4, 3.1459);
            //inSheet.addCell(number);
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }

    private void createOtherPitchingRow(WritableSheet inSheet, Element inPlayer, int inRow)
    {
        try
        {
            int i = 1;
            WritableCellFormat wkIntegerFormat = new WritableCellFormat(NumberFormats.INTEGER);
            wkIntegerFormat.setAlignment(Alignment.RIGHT);
            WritableCell wkCell = null;

            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("name").getText()));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("id").getText()));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_gs").getText()));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_ipfull").getText()));
            inSheet.addCell(new Label(i++, inRow, inPlayer.getChild("pitch_ipfract").getText()));
            
            for (int j=1 ; j < 6 ; j++)
            {
                wkCell = inSheet.getWritableCell(j, inRow);
                wkCell.setCellFormat(wkIntegerFormat);
            }
        }
        catch (WriteException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }    
    private void processSeriesStatFile(File inFile)
    {
        
        //Parse the file name to figure out the series and Reporting Team
        StringTokenizer wkName = new StringTokenizer(inFile.getName(),".");
        StringTokenizer wkParts = new StringTokenizer(wkName.nextToken(),"-");
        System.out.println("Processing file for year: " + wkParts.nextToken() + "   series: " + wkParts.nextToken() + "  reporting team: " + wkParts.nextToken());
        processTeamSeriesStatSheet(inFile);
        
        removeExportFile(inFile.getName());
        archiveFile(inFile.getName());
        
    }
    
    public void removeExportFile(String inFileName)
    {
        File wkDeleteFile = new File(exportFilesDir + "/series/" + inFileName);
        if (wkDeleteFile.exists())
        {
            boolean succeeded = wkDeleteFile.delete();
            _seriesExportFiles.remove(inFileName);
            
            if (succeeded)
            {
                System.out.println("removed Export " + inFileName);
            }
            else
            {
                System.out.println("Could not remove export file: " + inFileName);
            }
        }
    }
    
    public void archiveFile(String inFileName)
    {
        File wkMoveFile = new File(importFilesDir + "/series/" + inFileName);
        
        if (wkMoveFile.renameTo(new File(importFilesDir + "/archive/" + inFileName)))
        {
            System.out.println("moved good series " + inFileName);
        }
    }
    
    public void archiveErrorFile(String inFileName, Exception e)
    {
        File wkMoveFile = new File(importFilesDir + "/series/" + inFileName);
        Vector<String> wkFileName = new Vector<String>();
        wkFileName.add(importFilesDir + "/series/" + inFileName);
        try
        {
            Email.emailExceptionWithAttachments(e, wkFileName);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Email.emailException(ex);
        }
        
        
        if (wkMoveFile.renameTo(new File(importFilesDir + "/error/" + inFileName)))
        {
            System.out.println("error archived " + inFileName);
        }
    }
    
    
            
        
     
    public void processTeamSeriesStatSheet(File inFile)
    {        
        int  wkRow = 2;
        int wkYear = 0;;
        int wkSeries = 0;
        int wkReportingTeam = 0;
        int wkOtherTeam = 0;
        Workbook wkWorkbook = null;
        
        boolean wkGetOtherTeam = java.lang.Boolean.getBoolean("EnterOtherStats");
        
        try
        {
            System.out.println("START EXCEL IMPORT");
            wkWorkbook = Workbook.getWorkbook(inFile);
            Sheet wkSheet = wkWorkbook.getSheet("Team Stats");
            
            //skip label row
            wkRow++;
            
            //read series information
            
            System.out.println("year is " + wkSheet.getCell(1, wkRow).getContents());
            wkYear = getCellIntValue(wkSheet.getCell(1,wkRow));
            wkSeries = getCellIntValue(wkSheet.getCell(2,wkRow));
            wkReportingTeam = getCellIntValue(wkSheet.getCell(3,wkRow));
            wkOtherTeam = getCellIntValue(wkSheet.getCell(4,wkRow++));

            // skip blank row
            wkRow++;
            
            //skip batting label row
            wkRow++;
            
            Cell wkName = wkSheet.getCell(1, wkRow);
            
            while (null != wkName.getContents() && !wkName.getContents().equals(""))
            {
                //process all the batting player records
                int wkId = getCellIntValue(wkSheet.getCell(2,wkRow));
                SeriesStatRecord wkRecord = SeriesStatRecord.getSeriesStatRecord(wkId, wkYear, wkSeries, wkReportingTeam, wkReportingTeam);
                if (null != wkRecord)
                {
                    wkRecord.setGames(getCellIntValue(wkSheet.getCell(3, wkRow)));
                    wkRecord.setBat_ab(getCellIntValue(wkSheet.getCell(4, wkRow)));
                    wkRecord.setBat_runs(getCellIntValue(wkSheet.getCell(5, wkRow)));
                    wkRecord.setBat_hits(getCellIntValue(wkSheet.getCell(6, wkRow)));
                    wkRecord.setBat_rbi(getCellIntValue(wkSheet.getCell(7, wkRow)));
                    wkRecord.setBat_doubles(getCellIntValue(wkSheet.getCell(8, wkRow)));
                    wkRecord.setBat_triples(getCellIntValue(wkSheet.getCell(9, wkRow)));
                    wkRecord.setBat_hr(getCellIntValue(wkSheet.getCell(10, wkRow)));
                    wkRecord.setBat_walks(getCellIntValue(wkSheet.getCell(11, wkRow)));
                    wkRecord.setBat_strikeouts(getCellIntValue(wkSheet.getCell(12, wkRow)));
                    wkRecord.setBat_sb(getCellIntValue(wkSheet.getCell(13, wkRow)));
                    wkRecord.setBat_cs(getCellIntValue(wkSheet.getCell(14, wkRow)));
                    wkRecord.setBat_hbp(getCellIntValue(wkSheet.getCell(15, wkRow)));
                    wkRecord.setErrors(getCellIntValue(wkSheet.getCell(16, wkRow)));
                    
                    wkRecord.updateRecord();
                }
                    
                wkRow++;
                wkName = wkSheet.getCell(1, wkRow);
            }
               
            //skip blank area
            wkRow+=6;
            
            //skip pitching label row
            wkRow++;
            wkName = wkSheet.getCell(1, wkRow);
            while (null != wkName.getContents() && !wkName.getContents().equals(""))
            {
                //process all the pitching player records
                int wkId = getCellIntValue(wkSheet.getCell(2,wkRow));
                SeriesStatRecord wkRecord = SeriesStatRecord.getSeriesStatRecord(wkId, wkYear, wkSeries, wkReportingTeam, wkReportingTeam);
                if (null != wkRecord)
                {
                    wkRecord.setPitch_gp(getCellIntValue(wkSheet.getCell(3, wkRow)));
                    wkRecord.setPitch_gs(getCellIntValue(wkSheet.getCell(4, wkRow)));
                    wkRecord.setPitch_cg(getCellIntValue(wkSheet.getCell(5, wkRow)));
                    wkRecord.setPitch_sho(getCellIntValue(wkSheet.getCell(6, wkRow)));
                    wkRecord.setPitch_wins(getCellIntValue(wkSheet.getCell(7, wkRow)));
                    wkRecord.setPitch_loss(getCellIntValue(wkSheet.getCell(8, wkRow)));
                    wkRecord.setPitch_save(getCellIntValue(wkSheet.getCell(9, wkRow)));
                    wkRecord.setPitch_ipfull(getCellIntValue(wkSheet.getCell(10, wkRow)));
                    wkRecord.setPitch_ipfract(getCellIntValue(wkSheet.getCell(11, wkRow)));
                    wkRecord.setPitch_hits(getCellIntValue(wkSheet.getCell(12, wkRow)));
                    wkRecord.setPitch_runs(getCellIntValue(wkSheet.getCell(13, wkRow)));
                    wkRecord.setPitch_er(getCellIntValue(wkSheet.getCell(14, wkRow)));
                    wkRecord.setPitch_walks(getCellIntValue(wkSheet.getCell(15, wkRow)));
                    wkRecord.setPitch_strikeouts(getCellIntValue(wkSheet.getCell(16, wkRow)));
                    wkRecord.setPitch_hr(getCellIntValue(wkSheet.getCell(17, wkRow)));
                    
                    wkRecord.updateRecord();
                }
                    
                wkRow++;
                try
                {
                    wkName = wkSheet.getCell(1, wkRow);
                }
                catch (Exception wkEx)
                {
                break;
                }
            }
            
            if (wkGetOtherTeam)
            {
                //skip blank area
                wkRow+=3;
            
                 //skip pitching label row
                wkRow++;
            
                wkName = wkSheet.getCell(1, wkRow);
                while (null != wkName.getContents() && !wkName.getContents().equals(""))
                {
                    //process all the batting player records
                    int wkId = getCellIntValue(wkSheet.getCell(2,wkRow));
                    SeriesStatRecord wkRecord = SeriesStatRecord.getSeriesStatRecord(wkId, wkYear, wkSeries, wkReportingTeam, wkOtherTeam);
                    if (null != wkRecord)
                    {
                        wkRecord.setGames(getCellIntValue(wkSheet.getCell(3, wkRow)));                    
                        wkRecord.updateRecord();
                    }
                    
                    wkRow++;
                    wkName = wkSheet.getCell(1, wkRow);
                }
               
                //skip blank area
                wkRow+=6;
            
                //skip pitching label row
                wkRow++;
                wkName = wkSheet.getCell(1, wkRow);
                while (null != wkName.getContents() && !wkName.getContents().equals(""))
                {
                    //process all the pitching player records
                    int wkId = getCellIntValue(wkSheet.getCell(2,wkRow));
                    SeriesStatRecord wkRecord = SeriesStatRecord.getSeriesStatRecord(wkId, wkYear, wkSeries, wkReportingTeam, wkOtherTeam);
                    if (null != wkRecord)
                    {
                        wkRecord.setPitch_gs(getCellIntValue(wkSheet.getCell(3, wkRow)));
                        wkRecord.setPitch_ipfull(getCellIntValue(wkSheet.getCell(4, wkRow)));
                        wkRecord.setPitch_ipfract(getCellIntValue(wkSheet.getCell(5, wkRow)));
                    
                        wkRecord.updateRecord();
                    }
                    
                    wkRow++;
                    try
                    {
                        wkName = wkSheet.getCell(1, wkRow);
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException wkEx)
                    {
                        break;
                    }
                }
            }            
            statsManager.calculateTeamYearlyStats(wkYear, wkReportingTeam);
        }
        catch (IOException wkEx)
        {
            wkEx.printStackTrace();
            
            archiveErrorFile(inFile.getName(), wkEx);
        }        
        catch (jxl.read.biff.BiffException wkEx)
        {
            wkEx.printStackTrace();
            archiveErrorFile(inFile.getName(), wkEx);
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace();
            archiveErrorFile(inFile.getName(), wkEx);
        }
        finally
        {
            if (null != wkWorkbook)
                wkWorkbook.close();
        }
            
    }
    
    private int getCellIntValue(Cell inCell)
    {
        return Integer.parseInt(inCell.getContents());
        //NumberCell wkCell = (NumberCell) inCell;
        //return new Double(wkCell.getValue()).intValue();
    }
    
    
}
