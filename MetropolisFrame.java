import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MetropolisFrame extends JFrame {
    private final String host = "localhost";
    private final String userName = "nikasamadalashvili";
    private final String password = "Asdasd123";
    private final String database = "MetropolisesDb";
    private MetropolisTableModel tableModel;
    private JTextField metropolis;
    private JTextField continent;
    private JTextField population;
    private JComboBox<PopulationComboBoxModel> popComboBox;
    private JComboBox<KeywordComboBoxModel> keywordComboBox;

    public MetropolisFrame() {
        super("Metropolis Viewer");

        // YOUR CODE HERE

        // Could do this:
        // setLocationByPlatform(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        tableModel = new MetropolisTableModel(host, userName, password, database);
        var panel = new JPanel(new BorderLayout());
        var table = new JTable(tableModel);
        panel.add(new JScrollPane(table), "Center");
        var topPanel = new JPanel(new GridBagLayout());
        metropolis = new JTextField(20);
        JLabel metropolisLabel = new JLabel( "Metropolis: ");
        metropolisLabel.setLabelFor(metropolis);
        var c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        topPanel.add(metropolisLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        topPanel.add(metropolis, c);
        continent = new JTextField(20);
        JLabel continentLabel = new JLabel( "Continent: ");
        continentLabel.setLabelFor(continent);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        topPanel.add(continentLabel, c);
        c.weightx = 0.5;
        c.gridx = 3;
        topPanel.add(continent, c);
        population = new JTextField(20);
        JLabel populationLabel = new JLabel( "Population: ");
        populationLabel.setLabelFor(population);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 4;
        topPanel.add(populationLabel, c);
        c.weightx = 0.5;
        c.gridx = 5;
        topPanel.add(population, c);
        panel.add(topPanel, BorderLayout.NORTH);
        popComboBox = new JComboBox<PopulationComboBoxModel>(new PopulationComboBoxModel[] {
                new PopulationComboBoxModel("Population Larger Than", PopulationSearchType.GREATER),
                new PopulationComboBoxModel("Population Smaller Than", PopulationSearchType.SMALLER)
        });
        keywordComboBox = new JComboBox<KeywordComboBoxModel>(new KeywordComboBoxModel[] {
                new KeywordComboBoxModel("Exact Match", KeywordSearchType.EXACT),
                new KeywordComboBoxModel("Partial Match", KeywordSearchType.PARTIAL)
        });
        var searchButton = new JButton("Search");
        searchButton.addActionListener(actionEvent -> {
            tableModel.updateData(new MetropolisFilterModel(((PopulationComboBoxModel)popComboBox.getSelectedItem()).getPopulationSearchType(),
                    ((KeywordComboBoxModel)keywordComboBox.getSelectedItem()).getKeywordSearchType(), metropolis.getText(), continent.getText(), population.getText()));
        });
        var addButton = new JButton("Add");
        addButton.addActionListener(actionEvent -> {
            tableModel.addData(metropolis.getText(), continent.getText(), population.getText());
        });
        var rightPanel = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        rightPanel.add(addButton, c);
        c.gridx = 0;
        c.gridy = 1;
        rightPanel.add(searchButton, c);
        var comboPanel = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 1;
        comboPanel.add(popComboBox, c);
        c.gridx = 0;
        c.gridy = 2;
        comboPanel.add(keywordComboBox, c);
        c.gridx = 0;
        c.gridy = 2;
        comboPanel.setBorder(new TitledBorder("Search Options"));
        rightPanel.add(comboPanel, c);
        panel.add(rightPanel, BorderLayout.EAST);
        this.add(panel);
    }

    public static void main(String[] args) {
        // GUI Look And Feel
        // Do this incantation at the start of main() to tell Swing
        // to use the GUI LookAndFeel of the native platform. It's ok
        // to ignore the exception.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        MetropolisFrame frame = new MetropolisFrame();
    }

    private class PopulationComboBoxModel {
        private String displayName;
        private PopulationSearchType populationSearchType;

        private PopulationComboBoxModel(String displayName, PopulationSearchType populationSearchType) {
            this.displayName = displayName;
            this.populationSearchType = populationSearchType;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public PopulationSearchType getPopulationSearchType() {
            return populationSearchType;
        }
    }

    private class KeywordComboBoxModel {
        private String displayName;
        private KeywordSearchType keywordSearchType;

        private KeywordComboBoxModel(String displayName, KeywordSearchType keywordSearchType) {
            this.displayName = displayName;
            this.keywordSearchType = keywordSearchType;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public KeywordSearchType getKeywordSearchType() {
            return keywordSearchType;
        }
    }
}
