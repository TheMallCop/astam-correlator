////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s):
//              Denim Group, Ltd.
//              Secure Decisions, a division of Applied Visions, Inc
//
////////////////////////////////////////////////////////////////////////

package burp;

import burp.custombutton.ExportButton;
import burp.custombutton.LocalEndpointsButton;
import burp.custombutton.RemoteEndpointsButton;
import burp.extention.BurpPropertiesManager;
import burp.extention.RestUtils;
import com.denimgroup.threadfix.data.entities.Application;
import com.denimgroup.threadfix.data.enums.ParameterDataType;
import com.denimgroup.threadfix.data.interfaces.Endpoint;
import org.hibernate.resource.transaction.backend.jta.internal.JtaIsolationDelegate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: stran
 * Date: 12/30/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class BurpExtender implements IBurpExtender, ITab
{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private JTabbedPane tabbedPane;
    private JTextField urlField;
    private JTextField keyField;
    private JLabel apiErrorLabel;
    private Map<String, String> applicationMap = new HashMap<>();
    private JComboBox applicationComboBox;
    private JTextField sourceFolderField;
    private JTextField configFileField;
    private JTextField targetUrlField;
    private JCheckBox autoScanField;
    private JCheckBox autoSpiderField;
    private JLabel profMessage;
    private JLabel autoScanText;
    private JTextArea displayArea = new JTextArea();

    //
    // implement IBurpExtender
    //

    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks)
    {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;

        BurpPropertiesManager.generateBurpPropertiesManager(callbacks);

        // obtain an extension helpers object
        helpers = callbacks.getHelpers();

        // set our extension name
        callbacks.setExtensionName("Code PT");

        // create UI
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                tabbedPane = new JTabbedPane();

                JPanel mainPanel = buildMainPanel();
                JScrollPane mainScrollPane = new JScrollPane(mainPanel);
                tabbedPane.addTab("Main", mainScrollPane);

                JPanel optionsPanel = buildOptionsPanel();
                JScrollPane optionsScrollPane = new JScrollPane(optionsPanel);
                tabbedPane.addTab("Options", optionsScrollPane);

                // customize our UI components
                callbacks.customizeUiComponent(tabbedPane);

                // add the custom tab to Burp's UI
                callbacks.addSuiteTab(BurpExtender.this);
            }
        });
    }

    private JPanel buildMainPanel()
    {
        final JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new GridBagLayout());
        Insets mainPanelInsets = new Insets(10, 10, 10, 10);
        int yPosition = 0;

        JPanel importExportPanel = buildImportExportPanel();
        GridBagConstraints importExportPanelConstraints = new GridBagConstraints();
        importExportPanelConstraints.gridx = 0;
        importExportPanelConstraints.gridy = yPosition++;
        importExportPanelConstraints.ipadx = 5;
        importExportPanelConstraints.ipady = 5;
        importExportPanelConstraints.insets = mainPanelInsets;
        importExportPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(importExportPanel, importExportPanelConstraints);

        JScrollPane endpointTablePain = buildEndpointsTable();
        callbacks.customizeUiComponent(endpointTablePain);
        GridBagConstraints endpointTablePainConstraints = new GridBagConstraints();
        endpointTablePainConstraints.gridx = 0;
        endpointTablePainConstraints.gridy = yPosition++;
        endpointTablePainConstraints.insets = mainPanelInsets;
        endpointTablePainConstraints.fill = GridBagConstraints.HORIZONTAL;
        endpointTablePainConstraints.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(endpointTablePain, endpointTablePainConstraints);

        JSeparator importExportPanelSeparator = new JSeparator(JSeparator.HORIZONTAL);
        callbacks.customizeUiComponent(importExportPanelSeparator);
        GridBagConstraints importExportPanelSeparatorConstraints = new GridBagConstraints();
        importExportPanelSeparatorConstraints.gridx = 0;
        importExportPanelSeparatorConstraints.gridy = yPosition++;
        importExportPanelSeparatorConstraints.insets = mainPanelInsets;
        importExportPanelSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
        importExportPanelSeparatorConstraints.anchor = GridBagConstraints.NORTH;
        mainPanel.add(importExportPanelSeparator, importExportPanelSeparatorConstraints);

        JScrollPane displayPane = buildDisplayPane();
        callbacks.customizeUiComponent(displayPane);
        GridBagConstraints displayConstraints = new GridBagConstraints();
        displayConstraints.gridx = 0;
        displayConstraints.gridy = yPosition++;
        displayConstraints.insets = mainPanelInsets;
        displayConstraints.fill = GridBagConstraints.HORIZONTAL;
        displayConstraints.weightx = 1.0;
        displayConstraints.weighty = 1.0;
        displayConstraints.anchor = GridBagConstraints.NORTH;
        //displayConstraints.gridheight = 200;
        mainPanel.add(displayPane, displayConstraints);



        return mainPanel;
    }

    private JPanel buildOptionsPanel() {
        final JPanel optionsPanel = new JPanel();
        optionsPanel.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                boolean tabIsShowing = optionsPanel.isShowing();
                if (tabIsShowing) {
                    loadOptionsProperties();
                } else {
                    BurpPropertiesManager.getBurpPropertiesManager().saveProperties();
                }
            }
        });
        optionsPanel.setLayout(new GridBagLayout());
        Insets optionsPanelInsets = new Insets(10, 10, 10, 10);
        int yPosition = 0;

        JPanel autoOptionsPanel = buildAutoOptionsPanel();
        GridBagConstraints autoOptionsPanelConstraints = new GridBagConstraints();
        autoOptionsPanelConstraints.gridx = 0;
        autoOptionsPanelConstraints.gridy = yPosition++;
        autoOptionsPanelConstraints.ipadx = 5;
        autoOptionsPanelConstraints.ipady = 5;
        autoOptionsPanelConstraints.insets = optionsPanelInsets;
        autoOptionsPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        optionsPanel.add(autoOptionsPanel, autoOptionsPanelConstraints);

        JSeparator autoOptionsPanelSeparator = new JSeparator(JSeparator.HORIZONTAL);
        callbacks.customizeUiComponent(autoOptionsPanelSeparator);
        GridBagConstraints autoOptionsPanelSeparatorConstraints = new GridBagConstraints();
        autoOptionsPanelSeparatorConstraints.gridx = 0;
        autoOptionsPanelSeparatorConstraints.gridy = yPosition++;
        autoOptionsPanelSeparatorConstraints.insets = optionsPanelInsets;
        autoOptionsPanelSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
        autoOptionsPanelSeparatorConstraints.anchor = GridBagConstraints.NORTH;
        optionsPanel.add(autoOptionsPanelSeparator, autoOptionsPanelSeparatorConstraints);

        JPanel parametersPanel = buildParametersPanel();
        GridBagConstraints parametersPanelConstraints = new GridBagConstraints();
        parametersPanelConstraints.gridx = 0;
        parametersPanelConstraints.gridy = yPosition++;
        parametersPanelConstraints.ipadx = 5;
        parametersPanelConstraints.ipady = 5;
        parametersPanelConstraints.insets = optionsPanelInsets;
        parametersPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        optionsPanel.add(parametersPanel, parametersPanelConstraints);

        JSeparator parametersPanelSeparator = new JSeparator(JSeparator.HORIZONTAL);
        callbacks.customizeUiComponent(parametersPanelSeparator);
        GridBagConstraints parametersPanelSeparatorConstraints = new GridBagConstraints();
        parametersPanelSeparatorConstraints.gridx = 0;
        parametersPanelSeparatorConstraints.gridy = yPosition++;
        parametersPanelSeparatorConstraints.insets = optionsPanelInsets;
        parametersPanelSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
        parametersPanelSeparatorConstraints.anchor = GridBagConstraints.NORTH;
        optionsPanel.add(parametersPanelSeparator, parametersPanelSeparatorConstraints);

        JPanel sourcePanel = buildSourcePanel();
        GridBagConstraints sourcePanelConstraints = new GridBagConstraints();
        sourcePanelConstraints.gridx = 0;
        sourcePanelConstraints.gridy = yPosition++;
        sourcePanelConstraints.ipadx = 5;
        sourcePanelConstraints.ipady = 5;
        sourcePanelConstraints.insets = optionsPanelInsets;
        sourcePanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        optionsPanel.add(sourcePanel, sourcePanelConstraints);

        JSeparator sourcePanelSeparator = new JSeparator(JSeparator.HORIZONTAL);
        callbacks.customizeUiComponent(sourcePanelSeparator);
        GridBagConstraints sourcePanelSeparatorConstraints = new GridBagConstraints();
        sourcePanelSeparatorConstraints.gridx = 0;
        sourcePanelSeparatorConstraints.gridy = yPosition++;
        sourcePanelSeparatorConstraints.insets = optionsPanelInsets;
        sourcePanelSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
        sourcePanelSeparatorConstraints.anchor = GridBagConstraints.NORTH;
        optionsPanel.add(sourcePanelSeparator, sourcePanelSeparatorConstraints);

        JPanel configPanel = buildConfigPanel();
        GridBagConstraints configPanelConstraints = new GridBagConstraints();
        configPanelConstraints.gridx = 0;
        configPanelConstraints.gridy = yPosition++;
        configPanelConstraints.ipadx = 5;
        configPanelConstraints.ipady = 5;
        configPanelConstraints.insets = optionsPanelInsets;
        configPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        optionsPanel.add(configPanel, configPanelConstraints);

        JSeparator configPanelSeparator = new JSeparator(JSeparator.HORIZONTAL);
        callbacks.customizeUiComponent(configPanelSeparator);
        GridBagConstraints configPanelSeparatorConstraints = new GridBagConstraints();
        configPanelSeparatorConstraints.gridx = 0;
        configPanelSeparatorConstraints.gridy = yPosition++;
        configPanelSeparatorConstraints.insets = optionsPanelInsets;
        configPanelSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
        configPanelSeparatorConstraints.anchor = GridBagConstraints.NORTH;
        optionsPanel.add(configPanelSeparator, configPanelSeparatorConstraints);

        JPanel targetPanel = buildTargetPanel();
        GridBagConstraints targetPanelConstraints = new GridBagConstraints();
        targetPanelConstraints.gridx = 0;
        targetPanelConstraints.gridy = yPosition++;
        targetPanelConstraints.ipadx = 5;
        targetPanelConstraints.ipady = 5;
        targetPanelConstraints.insets = optionsPanelInsets;
        targetPanelConstraints.weightx = 1.0;
        targetPanelConstraints.weighty = 1.0;
        targetPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        optionsPanel.add(targetPanel, targetPanelConstraints);

        loadOptionsProperties();

        return optionsPanel;
    }


    private JScrollPane buildDisplayPane()
    {
        displayArea.setText("\n" + "\n" + "\n" + "\n" + "\n" + "\n");
        callbacks.customizeUiComponent(displayArea);
        displayArea.setEditable(false);
        return new JScrollPane(displayArea);

    }

    private JScrollPane buildEndpointsTable()
    {
        Object[][] data = {};
        String[] columnNames =
                {"Detected Endpoints",
                "Number of Detected Parameters",
                "GET Method",
                "POST Method",
                "Endpoint"
                };

        DefaultTableModel dtm = new DefaultTableModel(data, columnNames){

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        JTable endpointsTable = new JTable(dtm);
        endpointsTable.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //displayArea.setText(endpointsTable.getModel().getValueAt(endpointsTable.getSelectedRow(), 4).toString());
                Endpoint.Info endpoint = (Endpoint.Info)endpointsTable.getModel().getValueAt(endpointsTable.getSelectedRow(), 4);
                displayArea.setText("URL:" + "\n");
                displayArea.append(endpoint.getUrlPath() + "\n" + "\n");
                displayArea.append("Methods: " + "\n" );
                Iterator<String> it = endpoint.getHttpMethods().iterator();
                while (it.hasNext())
                {
                    displayArea.append(it.next() + "\n");
                }

                displayArea.append("\n" + "Parameters and type:" + "\n");
                for(Map.Entry<String, ParameterDataType> parameter : endpoint.getParameters().entrySet())
                {
                   displayArea.append(parameter.getKey() + " - " + parameter.getValue() + "\n");
                }


            }

            @Override
            public void mousePressed(MouseEvent e)
            {

            }

            @Override
            public void mouseReleased(MouseEvent e)
            {

            }

            @Override
            public void mouseEntered(MouseEvent e)
            {

            }

            @Override
            public void mouseExited(MouseEvent e)
            {

            }
        });

        TableColumn tc = endpointsTable.getColumnModel().getColumn(2);
        tc.setCellEditor(endpointsTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(endpointsTable.getDefaultRenderer(Boolean.class));
        tc = endpointsTable.getColumnModel().getColumn(3);
        tc.setCellEditor(endpointsTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(endpointsTable.getDefaultRenderer(Boolean.class));
        endpointsTable.getColumnModel().getColumn(4).setMinWidth(0);
        endpointsTable.getColumnModel().getColumn(4).setMaxWidth(0);
        endpointsTable.getColumnModel().getColumn(4).setWidth(0);
        JScrollPane endpointsTablePane = new JScrollPane(endpointsTable);
        endpointsTable.setFillsViewportHeight(true);
        callbacks.customizeUiComponent(endpointsTable);
        BurpPropertiesManager.getBurpPropertiesManager().setEndpointsTable(endpointsTable);
        return endpointsTablePane;
    }

    private JPanel buildImportExportPanel()
    {
        JPanel importExportPanel = new JPanel();
        importExportPanel.setLayout(new GridBagLayout());
        int yPosition = 0;

        addPanelTitleToGridBagLayout("Code PT Source Code Analysis", importExportPanel, yPosition++);
        addPanelDescriptionToGridBagLayout("Use Code PT to analyize the server side source code to detect endpoints and parameters and import them into Burp." , importExportPanel, yPosition++);
        addPanelDescriptionToGridBagLayout("These results may include URL endpoints and optional parameters a spider may not find." , importExportPanel, yPosition++);

        JButton localEndpointsButton = new LocalEndpointsButton(getUiComponent(), callbacks);
        callbacks.customizeUiComponent(localEndpointsButton);

        JButton remoteEndpointsButton = new RemoteEndpointsButton(getUiComponent(), callbacks);
        callbacks.customizeUiComponent(remoteEndpointsButton);

        JButton exportButton = new ExportButton(getUiComponent(), callbacks);
        callbacks.customizeUiComponent(exportButton);

        localEndpointsButton.setSize(300, 30);
        localEndpointsButton.setLocation(10,400);
        remoteEndpointsButton.setSize(300, 30);
        remoteEndpointsButton.setLocation(320,400);
        exportButton.setSize(300, 30);
        exportButton.setLocation(630,400);

        GridBagConstraints gridBagConstraintsLocal = new GridBagConstraints();
        gridBagConstraintsLocal.gridwidth = 1;
        gridBagConstraintsLocal.gridx = 1;
        gridBagConstraintsLocal.gridy = ++yPosition;
        gridBagConstraintsLocal.ipadx = 5;
        gridBagConstraintsLocal.ipady = 5;
        gridBagConstraintsLocal.anchor = GridBagConstraints.NORTHWEST;



        GridBagConstraints gridBagConstraintsRemote = new GridBagConstraints();
        gridBagConstraintsRemote.gridwidth = 1;
        gridBagConstraintsRemote.gridx = 2;
        gridBagConstraintsRemote.gridy = yPosition;
        gridBagConstraintsRemote.ipadx = 5;
        gridBagConstraintsRemote.ipady = 5;
        gridBagConstraintsRemote.anchor = GridBagConstraints.NORTHWEST;

        GridBagConstraints gridBagConstraintsExport = new GridBagConstraints();
        gridBagConstraintsExport.gridwidth = 0;
        gridBagConstraintsExport.gridx = 3;
        gridBagConstraintsExport.gridy = yPosition;
        gridBagConstraintsExport.ipadx = 5;
        gridBagConstraintsExport.ipady = 5;
        gridBagConstraintsExport.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraintsExport.weightx = 1;


        importExportPanel.add(localEndpointsButton, gridBagConstraintsLocal);
        importExportPanel.add(remoteEndpointsButton, gridBagConstraintsRemote);
        importExportPanel.add(exportButton, gridBagConstraintsExport);


        return importExportPanel;
    }

    private JPanel buildParametersPanel() {
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridBagLayout());
        int yPosition = 0;

        Runnable applicationComboBoxRunnable = new Runnable() {
            @Override
            public void run() {
                updateApplicationComboBox(applicationMap, apiErrorLabel, applicationComboBox);
            }
        };
        ActionListener applicationComboBoxActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String applicationName = (String) applicationComboBox.getSelectedItem();
                String applicationId = applicationMap.get(applicationName);
                BurpPropertiesManager.getBurpPropertiesManager().setPropertyValue(BurpPropertiesManager.APP_ID_KEY, applicationId);
            }
        };

        final JLabel parametersPanelTitle = addPanelTitleToGridBagLayout("Correlation Server", parametersPanel, yPosition++);
        final JLabel parametersPanelDescription = addPanelDescriptionToGridBagLayout("These settings let you connect to a Correlation server and choose an Application.", parametersPanel, yPosition++);
        urlField = addTextFieldToGridBagLayout("Correlation Server URL:", parametersPanel, yPosition++, BurpPropertiesManager.THREADFIX_URL_KEY, applicationComboBoxRunnable);
        keyField = addTextFieldToGridBagLayout("API Key:", parametersPanel, yPosition++, BurpPropertiesManager.API_KEY_KEY, applicationComboBoxRunnable);

        final JButton applicationComboBoxRefreshButton = new JButton("Refresh application list");
        applicationComboBoxRefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateApplicationComboBox(applicationMap, apiErrorLabel, applicationComboBox);
            }
        });
        applicationComboBox = addComboBoxToGridBagLayout("Pick an Application", parametersPanel, yPosition++, applicationComboBoxActionListener, applicationComboBoxRefreshButton);
        apiErrorLabel = addErrorMessageToGridBagLayout(parametersPanel, yPosition++);

        return parametersPanel;
    }

    private JPanel buildSourcePanel() {
        final JPanel sourcePanel = new JPanel();
        sourcePanel.setLayout(new GridBagLayout());
        int yPosition = 0;

        final JLabel sourcePanelTitle = addPanelTitleToGridBagLayout("Local Source Code", sourcePanel, yPosition++);
        final JLabel sourcePanelDescription = addPanelDescriptionToGridBagLayout("This setting lets you configure the location of your source code.", sourcePanel, yPosition++);

        final JButton sourceFolderBrowseButton = new JButton("Select folder ...");
        sourceFolderBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                String currentDirectory = sourceFolderField.getText();
                if ((currentDirectory == null) || (currentDirectory.trim().equals(""))) {
                    currentDirectory = System.getProperty("user.home");
                }
                chooser.setCurrentDirectory(new java.io.File(currentDirectory));
                chooser.setDialogTitle("Please select the folder containing the source code");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(sourcePanel) == JFileChooser.APPROVE_OPTION) {
                    sourceFolderField.setText(chooser.getSelectedFile().getAbsolutePath());
                    BurpPropertiesManager.getBurpPropertiesManager().setSourceFolder(sourceFolderField.getText());
                }
            }
        });
        sourceFolderField = addTextFieldToGridBagLayout("Location of source code folder:", sourcePanel, yPosition++, BurpPropertiesManager.SOURCE_FOLDER_KEY, sourceFolderBrowseButton);

        return sourcePanel;
    }

    private JPanel buildConfigPanel() {
        final JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridBagLayout());
        int yPosition = 0;

        final JLabel configPanelTitle = addPanelTitleToGridBagLayout("Burp Configuration File", configPanel, yPosition++);
        final JLabel configPanelDescription = addPanelDescriptionToGridBagLayout("This setting lets you configure the location of your Burp configuration file.", configPanel, yPosition++);

        final JButton configFileBrowseButton = new JButton("Select file ...");
        configFileBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                String currentDirectory = configFileField.getText();
                if ((currentDirectory == null) || (currentDirectory.trim().equals(""))) {
                    currentDirectory = System.getProperty("user.home");
                }
                chooser.setCurrentDirectory(new java.io.File(currentDirectory));
                chooser.setDialogTitle("Please select the burp configuration file");
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(configPanel) == JFileChooser.APPROVE_OPTION) {
                    configFileField.setText(chooser.getSelectedFile().getAbsolutePath());
                    BurpPropertiesManager.getBurpPropertiesManager().setConfigFile(configFileField.getText());
                }
            }
        });
        configFileField = addTextFieldToGridBagLayout("Location of configuration file :", configPanel, yPosition++, BurpPropertiesManager.CONFIG_FILE_KEY, configFileBrowseButton);

        return configPanel;
    }

    private JPanel buildTargetPanel() {
        final JPanel targetPanel = new JPanel();
        targetPanel.setLayout(new GridBagLayout());
        int yPosition = 0;

        final JLabel targetPanelTitle = addPanelTitleToGridBagLayout("Target URL", targetPanel, yPosition++);
        targetUrlField = addTextFieldToGridBagLayout("Please enter the target URL:", targetPanel, yPosition++, BurpPropertiesManager.TARGET_URL_KEY);
        return targetPanel;
    }

    private JPanel buildAutoOptionsPanel() {
        final JPanel autoOptionsPanel = new JPanel();
        autoOptionsPanel.setLayout(new GridBagLayout());
        int yPosition = 0;

        final JLabel autoOptionsPanelTitle = addPanelTitleToGridBagLayout("Code PT Plugin Behavior", autoOptionsPanel, yPosition++);
        ActionListener applicationCheckBoxSpiderActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BurpPropertiesManager.getBurpPropertiesManager().setAutoSpider(autoSpiderField.isSelected());
                if(BurpPropertiesManager.getBurpPropertiesManager().getAutoSpider() && callbacks.getBurpVersion()[0].toLowerCase().contains("professional"))
                {
                    autoScanField.setEnabled(true);
                    autoScanText.setForeground(Color.BLACK);
                }
                else
                {
                    autoScanField.setEnabled(false);
                    BurpPropertiesManager.getBurpPropertiesManager().setAutoScan(false);
                    autoScanField.setSelected(false);
                    autoScanText.setForeground(Color.GRAY);
                }
            }
        };

        ActionListener applicationCheckBoxScanActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BurpPropertiesManager.getBurpPropertiesManager().setAutoScan(autoScanField.isSelected());
            }
        };
        autoScanText = new JLabel("Automatically start active scanner after automatic spider: ");
        autoSpiderField = addCheckBoxToGridBagLayout("Automatically start spider after importing endpoints: ", autoOptionsPanel, yPosition++, applicationCheckBoxSpiderActionListener);
        if(callbacks.getBurpVersion()[0].toLowerCase().contains("professional"))
        {autoScanField = addCheckBoxToGridBagLayout(autoScanText, autoOptionsPanel, yPosition++, applicationCheckBoxScanActionListener);
            if (BurpPropertiesManager.getBurpPropertiesManager().getAutoSpider())
            {
                autoScanField.setEnabled(true);
                autoScanText.setForeground(Color.BLACK);
            }
            else
            {
                autoScanText.setForeground(Color.GRAY);
                autoScanField.setEnabled(false);
            }
        }
        else
        {
            autoScanField = addCheckBoxToGridBagLayout(autoScanText, autoOptionsPanel, yPosition++, applicationCheckBoxScanActionListener);
            autoScanField.setEnabled(false);
            profMessage = addPanelLabelToGridBagLayout("\t *Note this option is only available with the Pro Version", autoOptionsPanel, yPosition++);
        }

        return autoOptionsPanel;
    }

    //
    // implement ITab
    //

    @Override
    public String getTabCaption()
    {
        return "Code PT";
    }

    @Override
    public Component getUiComponent()
    {
        return tabbedPane;
    }

    private class ThreadFixPropertyFieldListener implements DocumentListener, FocusListener {
        private JTextField jTextField;
        private String propertyName;
        private Runnable runnable;

        private String lastValue = null;

        public ThreadFixPropertyFieldListener(JTextField jTextField, String propertyName) {
            this(jTextField, propertyName, null);
        }

        public ThreadFixPropertyFieldListener(JTextField jTextField, String propertyName, Runnable runnable) {
            this.jTextField = jTextField;
            this.propertyName = propertyName;
            this.runnable = runnable;
        }

        protected void update() {
            BurpPropertiesManager.getBurpPropertiesManager().setPropertyValue(propertyName, jTextField.getText().trim());
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void focusGained(FocusEvent e) {
            lastValue = jTextField.getText().trim();
        }

        @Override
        public void focusLost(FocusEvent e) {
            String currentValue = jTextField.getText().trim();
            if (!currentValue.equals(lastValue)) {
                update();
            }
        }
    }

    private void loadOptionsProperties() {
        BurpPropertiesManager burpPropertiesManager = BurpPropertiesManager.getBurpPropertiesManager();
        urlField.setText(burpPropertiesManager.getUrl());
        keyField.setText(burpPropertiesManager.getKey());
        updateApplicationComboBox(applicationMap, apiErrorLabel, applicationComboBox);
        sourceFolderField.setText(burpPropertiesManager.getSourceFolder());
        configFileField.setText(burpPropertiesManager.getConfigFile());
        targetUrlField.setText(burpPropertiesManager.getTargetUrl());
    }

    private void updateApplicationComboBox(Map<String, String> applicationMap, JLabel apiErrorLabel, JComboBox applicationComboBox) {
        applicationComboBox.setEnabled(false);
        ActionListener[] applicationComboBoxActionListeners = applicationComboBox.getActionListeners();
        for (ActionListener applicationComboBoxActionListener : applicationComboBoxActionListeners) {
            applicationComboBox.removeActionListener(applicationComboBoxActionListener);
        }

        updateApplicationMapData(applicationMap);
        Object[] possibilities = applicationMap.keySet().toArray();

        boolean failedToConnect = false;
        String failureMessage = "";
        if (possibilities.length != 0 && possibilities[0].toString().startsWith("Authentication failed")) {
            failedToConnect = true;
            failureMessage = possibilities[0].toString();
        }
        else if (possibilities.length == 0) {
            failedToConnect = true;
            failureMessage = "Failed while trying to get a list of applications from the correlation server.";
        }

        String currentAppId = BurpPropertiesManager.getBurpPropertiesManager().getAppId();
        applicationComboBox.removeAllItems();
        if (failedToConnect) {
            apiErrorLabel.setText(failureMessage);
        } else {
            apiErrorLabel.setText("");
            for (Object possibility : possibilities) {
                applicationComboBox.addItem(possibility);
            }
            String currentAppName = applicationMap.get(currentAppId);
            for (String appName : applicationMap.keySet()) {
                if(applicationMap.get(appName).equals(currentAppId)) {
                    currentAppName = appName;
                    break;
                }
            }
            applicationComboBox.setSelectedItem(currentAppName);
        }
        for (ActionListener applicationComboBoxActionListener : applicationComboBoxActionListeners) {
            applicationComboBox.addActionListener(applicationComboBoxActionListener);
        }
        applicationComboBox.setEnabled(!failedToConnect);
    }

    private void updateApplicationMapData(Map<String, String> applicationMap) {
        Application.Info[] infos;
        try {
            infos = RestUtils.getApplications();
        } catch (Exception e) {
            infos = new Application.Info[0];
        }

        applicationMap.clear();
        for (Application.Info info : infos) {
            applicationMap.put(info.getOrganizationName() + "/" + info.getApplicationName(),
                    info.getApplicationId());
        }
    }

    private JLabel addPanelTitleToGridBagLayout(String titleText, Container gridBagContainer, int yPosition) {
        final JLabel panelTitle = new JLabel(titleText, JLabel.LEFT);
        panelTitle.setForeground(new Color(236, 136, 0));
        Font font = panelTitle.getFont();
        panelTitle.setFont(new Font(font.getFontName(), font.getStyle(), font.getSize() + 4));
        panelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        callbacks.customizeUiComponent(panelTitle);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagContainer.add(panelTitle, gridBagConstraints);
        return panelTitle;
    }

    private JLabel addPanelLabelToGridBagLayout(String titleText, Container gridBagContainer, int yPosition) {
        final JLabel panelTitle = new JLabel(titleText);
        panelTitle.setForeground(new Color(236, 136, 0));
        Font font = panelTitle.getFont();
        panelTitle.setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
        panelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        callbacks.customizeUiComponent(panelTitle);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagContainer.add(panelTitle, gridBagConstraints);
        return panelTitle;
    }

    private JLabel addPanelDescriptionToGridBagLayout(String descriptionText, Container gridBagContainer, int yPosition) {
        final JLabel panelDescription = new JLabel(descriptionText);
        panelDescription.setHorizontalAlignment(SwingConstants.LEFT);
        callbacks.customizeUiComponent(panelDescription);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagContainer.add(panelDescription, gridBagConstraints);
        return panelDescription;
    }

    private JLabel addErrorMessageToGridBagLayout(Container gridBagContainer, int yPosition) {
        final JLabel errorMessage = new JLabel("");
        errorMessage.setForeground(new Color(255, 0, 0));
        errorMessage.setHorizontalAlignment(SwingConstants.LEFT);
        callbacks.customizeUiComponent(errorMessage);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagContainer.add(errorMessage, gridBagConstraints);
        return errorMessage;
    }

    private JTextField addTextFieldToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, String propertyKey) {
        return addTextFieldToGridBagLayout(labelText, gridBagContainer, yPosition, propertyKey, null, null);
    }

    private JTextField addTextFieldToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, String propertyKey, Runnable threadFixPropertyFieldListenerRunnable) {
        return addTextFieldToGridBagLayout(labelText, gridBagContainer, yPosition, propertyKey, threadFixPropertyFieldListenerRunnable, null);
    }

    private JTextField addTextFieldToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, String propertyKey, JButton button) {
        return addTextFieldToGridBagLayout(labelText, gridBagContainer, yPosition, propertyKey, null, button);
    }

    private JTextField addTextFieldToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, String propertyKey, Runnable threadFixPropertyFieldListenerRunnable, JButton button) {
        JLabel textFieldLabel = new JLabel(labelText);
        callbacks.customizeUiComponent(textFieldLabel);
        textFieldLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagContainer.add(textFieldLabel, gridBagConstraints);

        JTextField textField = new JTextField(40);
        callbacks.customizeUiComponent(textField);
        textField.addFocusListener(new ThreadFixPropertyFieldListener(textField, propertyKey, threadFixPropertyFieldListenerRunnable));
        gridBagConstraints = new GridBagConstraints();
        if (button == null) {
            gridBagConstraints.gridwidth = 2;
        } else {
            gridBagConstraints.gridwidth = 1;
        }
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagContainer.add(textField, gridBagConstraints);

        if (button != null) {
            callbacks.customizeUiComponent(button);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = yPosition;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 5;
            gridBagConstraints.fill = GridBagConstraints.NONE;
            gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
            gridBagContainer.add(button, gridBagConstraints);
        }

        return textField;
    }


    private JComboBox addComboBoxToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, ActionListener actionListener) {
        return addComboBoxToGridBagLayout(labelText, gridBagContainer, yPosition, actionListener, null);
    }

    private JComboBox addComboBoxToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, ActionListener actionListener, JButton button) {
        JLabel textFieldLabel = new JLabel(labelText);
        callbacks.customizeUiComponent(textFieldLabel);
        textFieldLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagContainer.add(textFieldLabel, gridBagConstraints);

        JComboBox comboBox = new JComboBox();
        comboBox.setEnabled(false);
        callbacks.customizeUiComponent(comboBox);
        gridBagConstraints = new GridBagConstraints();
        if (button == null) {
            gridBagConstraints.gridwidth = 2;
        } else {
            gridBagConstraints.gridwidth = 1;
        }
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagContainer.add(comboBox, gridBagConstraints);

        if (button != null) {
            callbacks.customizeUiComponent(button);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = yPosition;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 5;
            gridBagConstraints.fill = GridBagConstraints.NONE;
            gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
            gridBagContainer.add(button, gridBagConstraints);
        }

        comboBox.addActionListener(actionListener);

        return comboBox;
    }

    private JCheckBox addCheckBoxToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, ActionListener actionListener) {
        return addCheckBoxToGridBagLayout(labelText, gridBagContainer, yPosition, actionListener, null);
    }

    private JCheckBox addCheckBoxToGridBagLayout(JLabel label, Container gridBagContainer, int yPosition, ActionListener actionListener) {
        return addCheckBoxToGridBagLayout(label, gridBagContainer, yPosition, actionListener, null);
    }

    private JCheckBox addCheckBoxToGridBagLayout(String labelText, Container gridBagContainer, int yPosition, ActionListener actionListener, JButton button) {
        JLabel textFieldLabel = new JLabel(labelText);
        callbacks.customizeUiComponent(textFieldLabel);

        textFieldLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagContainer.add(textFieldLabel, gridBagConstraints);

        JCheckBox checkBox = new JCheckBox();
        callbacks.customizeUiComponent(checkBox);
        gridBagConstraints = new GridBagConstraints();
        if (button == null) {
            gridBagConstraints.gridwidth = 2;
        } else {
            gridBagConstraints.gridwidth = 1;
        }
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagContainer.add(checkBox, gridBagConstraints);

        if (button != null) {
            callbacks.customizeUiComponent(button);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = yPosition;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 5;
            gridBagConstraints.fill = GridBagConstraints.NONE;
            gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
            gridBagContainer.add(button, gridBagConstraints);
        }

        checkBox.addActionListener(actionListener);

        return checkBox;
    }

    private JCheckBox addCheckBoxToGridBagLayout(JLabel textFieldLabel, Container gridBagContainer, int yPosition, ActionListener actionListener, JButton button) {
        callbacks.customizeUiComponent(textFieldLabel);

        textFieldLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagContainer.add(textFieldLabel, gridBagConstraints);

        JCheckBox checkBox = new JCheckBox();
        callbacks.customizeUiComponent(checkBox);
        gridBagConstraints = new GridBagConstraints();
        if (button == null) {
            gridBagConstraints.gridwidth = 2;
        } else {
            gridBagConstraints.gridwidth = 1;
        }
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = yPosition;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagContainer.add(checkBox, gridBagConstraints);

        if (button != null) {
            callbacks.customizeUiComponent(button);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = yPosition;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 5;
            gridBagConstraints.fill = GridBagConstraints.NONE;
            gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
            gridBagContainer.add(button, gridBagConstraints);
        }

        checkBox.addActionListener(actionListener);

        return checkBox;
    }
}