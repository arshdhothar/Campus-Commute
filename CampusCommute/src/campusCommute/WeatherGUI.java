package campusCommute;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;

public class WeatherGUI extends JFrame {
	private WeatherInfo info;
    private final JTextField cityField = new JTextField(20);
    private final JButton fetchButton = new JButton("Get Weather");
    private final JButton openWebButton = new JButton("Open on WeatherNetwork");
    
    private final JLabel statusLabel = new JLabel(" ");
    private final JPanel infoPanel = new JPanel();

    public WeatherGUI() {
        super("CampusCommute — Weather");

        // Top input bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("City:"));
        top.add(cityField);
        top.add(fetchButton);
        top.add(openWebButton);
        openWebButton.setEnabled(false); // disabled until weather info loaded


        // Info panel (starts empty)
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(8, 12, 8, 12));
        statusLabel.setForeground(new Color(90, 90, 90));
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Root layout
        setLayout(new BorderLayout(0, 0));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(infoPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
            BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Button action
        fetchButton.addActionListener(e -> fetchWeather());
        openWebButton.addActionListener(e -> openWeatherNetwork());
        // Window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);
    }

    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city.", "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setBusy(true);
        statusLabel.setText("Fetching weather…");

        new SwingWorker<WeatherInfo, Void>() {
            @Override
            protected WeatherInfo doInBackground() {
                info = WeatherAPI.getWeather(city);
                return info;
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    WeatherInfo info = get();
                    if (info == null) {
                        statusLabel.setText("Failed to fetch weather. Check API key or network.");
                        renderEmpty();
                    } else {
                        statusLabel.setText("Updated: " + info.getTimeDate());
                        renderInfo(info);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    renderEmpty();
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        fetchButton.setEnabled(!busy);
        cityField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void renderEmpty() {
        infoPanel.removeAll();
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private void renderInfo(WeatherInfo info) {
        infoPanel.removeAll();
        openWebButton.setEnabled(true);
        
        // Nice card-like layout
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JLabel title = new JLabel(info.getCity());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        infoPanel.add(title, gc);

        gc.gridy++;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(1, 1));
        infoPanel.add(sep, gc);

        // Add rows
        gc.gridy++;
        infoPanel.add(row("Local Time", info.getTimeDate()), gc);

        gc.gridy++;
        infoPanel.add(row("Temperature", safe(info.getTemp()) + " °C"), gc);

        gc.gridy++;
        infoPanel.add(row("Feels Like", safe(info.getFeelsLike()) + " °C"), gc);

        gc.gridy++;
        infoPanel.add(row("Condition", safe(info.getCondition())), gc);

        gc.gridy++;
        infoPanel.add(row("Is Day", "1".equals(info.getIsDay()) ? "Yes" : "No"), gc);

        gc.gridy++;
        infoPanel.add(row("Wind Speed", safe(info.getWindSpeed()) + " km/h"), gc);

        gc.gridy++;
        infoPanel.add(row("Humidity", safe(info.getHumidity()) + "%"), gc);

        gc.gridy++;
        infoPanel.add(row("UV Index", safe(info.getUv())), gc);

        gc.gridy++;
        infoPanel.add(row("EPA AQI (US)", safe(info.getEpaIndex())), gc);

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel row(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(6, 8, 6, 8));
        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        JLabel v = new JLabel(value);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        p.setBackground(new Color(248, 249, 251));
        p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 232, 236)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return p;
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "N/A" : s;
    }
    private String buildWeatherNetworkURL(String city, String province) {
        String cityFormatted = city.toLowerCase().replace(" ", "-");
        String provinceFormatted = province.toLowerCase().replace(" ", "-");

        return String.format(
            "https://www.theweathernetwork.com/en/city/ca/%s/%s/hourly",
            provinceFormatted,
            cityFormatted
        );
    }
    private void openWeatherNetwork() {
        String city = cityField.getText().trim();
        
        // Province from WeatherAPI:
        String province = info.getProvince();  // ⬅ We must store this (see below)

        if (city.isBlank() || province == null) {
            JOptionPane.showMessageDialog(this,
                    "Weather not loaded yet.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String url = buildWeatherNetworkURL(city, province);

        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not open browser.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherGUI().setVisible(true));
    }
}
