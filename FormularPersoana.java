import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class FormularPersoana extends JFrame {
    private JTextField numeTextField;
    private JCheckBox casatoritCheckBox;
    private JRadioButton masculinRadioButton, femininRadioButton;
    private JComboBox<String> varstaComboBox;
    private JTextArea observatiiTextArea;
    private DefaultTableModel tableModel;
    private JTable dataTable;

    public FormularPersoana() {
        super("Formular Persoana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crează panouri pentru formular și butoane
        JPanel formularPanel = new JPanel(new GridLayout(7, 2));
        JPanel buttonPanel = new JPanel();

        // Adaugă componente la panoul formularului
        formularPanel.add(new JLabel("Nume:"));
        numeTextField = new JTextField();
        formularPanel.add(numeTextField);

        formularPanel.add(new JLabel("Căsătorit:"));
        casatoritCheckBox = new JCheckBox();
        formularPanel.add(casatoritCheckBox);

        formularPanel.add(new JLabel("Gen:"));
        ButtonGroup genButtonGroup = new ButtonGroup();
        masculinRadioButton = new JRadioButton("Masculin");
        femininRadioButton = new JRadioButton("Feminin");
        genButtonGroup.add(masculinRadioButton);
        genButtonGroup.add(femininRadioButton);
        formularPanel.add(masculinRadioButton);
        formularPanel.add(femininRadioButton);

        formularPanel.add(new JLabel("Vârsta:"));
        String[] varste = {"18-25", "26-35", "36-50", "51+"};
        varstaComboBox = new JComboBox<>(varste);
        formularPanel.add(varstaComboBox);

        formularPanel.add(new JLabel("Observații:"));
        observatiiTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(observatiiTextArea);
        formularPanel.add(scrollPane);

        // Creează tabelul și modelul acestuia
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Nume");
        tableModel.addColumn("Căsătorit");
        tableModel.addColumn("Gen");
        tableModel.addColumn("Vârsta");
        tableModel.addColumn("Observații");
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);

        // Adaugă componente la panoul butoanelor
        JButton salvareButton = new JButton("Salvare");
        salvareButton.addActionListener(new

                                                ActionListener() {
                                                    @Override
                                                    public void actionPerformed (ActionEvent e){
                                                        salvareDateJSON();
                                                    }
                                                });
        buttonPanel.add(salvareButton);

        JButton anulareButton = new JButton("Anulare");
        anulareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(anulareButton);

        // Adaugă panourile la frame-ul principal
        add(formularPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Încarcă datele JSON la pornirea programului
        incarcaDateJSON();

        setSize(600, 400);
        setVisible(true);
    }
    private void clearForm() {
        numeTextField.setText("");
        casatoritCheckBox.setSelected(false);
        masculinRadioButton.setSelected(false);
        femininRadioButton.setSelected(false);
        varstaComboBox.setSelectedIndex(0);
        observatiiTextArea.setText("");
    }
    private void incarcaDateJSON() {
        try (FileReader fileReader = new FileReader("date.json")) {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(fileReader);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String nume = (String) jsonObject.get("Nume");
                boolean casatorit = (boolean) jsonObject.get("Căsătorit");
                String gen = (String) jsonObject.get("Gen");
                String varsta = (String) jsonObject.get("Vârsta");
                String observatii = (String) jsonObject.get("Observații");

                // Adaugă rând în tabel
                tableModel.addRow(new Object[]{nume, casatorit, gen, varsta, observatii});
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea datelor JSON!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void actualizeazaTabelulDirectDinJSON() {
        try (FileReader fileReader = new FileReader("date.json")) {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(fileReader);

            // Elimină toate rândurile existente din modelul tabelului
            tableModel.setRowCount(0);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String nume = (String) jsonObject.get("Nume");
                boolean casatorit = (boolean) jsonObject.get("Căsătorit");
                String gen = (String) jsonObject.get("Gen");
                String varsta = (String) jsonObject.get("Vârsta");
                String observatii = (String) jsonObject.get("Observații");

                // Adaugă rând în tabel
                tableModel.addRow(new Object[]{nume, casatorit, gen, varsta, observatii});
                System.out.println("Actualizare tabel din JSON");

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea datelor JSON!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void salvareDateJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Nume", numeTextField.getText());
        jsonObject.put("Căsătorit", casatoritCheckBox.isSelected());
        jsonObject.put("Gen", masculinRadioButton.isSelected() ? "Masculin" : "Feminin");
        jsonObject.put("Vârsta", varstaComboBox.getSelectedItem());
        jsonObject.put("Observații", observatiiTextArea.getText());

        try (FileReader fileReader = new FileReader("date.json")) {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(fileReader);

            try (FileWriter file = new FileWriter("date.json", false)) {
                if (!jsonArray.isEmpty()) {
                    // Dacă există deja obiecte în fișier, adaugă o virgulă
                    file.write(jsonArray.toJSONString().substring(0, jsonArray.toJSONString().length() - 1));
                    file.write(",");
                }

                // Scrie obiectul JSON curent
                file.write(jsonObject.toJSONString());

                // Inchide paranteza patrata la final (dacă există obiecte)
                if (!jsonArray.isEmpty()) {
                    file.write("]");
                }

                JOptionPane.showMessageDialog(this, "Datele au fost salvate cu succes!");
                clearForm();

                // Actualizează tabela direct din fișierul JSON
                SwingUtilities.invokeLater(() -> actualizeazaTabelulDirectDinJSON());

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la salvarea datelor!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }





}

