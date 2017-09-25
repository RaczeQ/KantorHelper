import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static java.lang.Math.ceil;
import static java.lang.Math.round;


/**
 * Created by RaczeQ on 07/02/2017.
 */
public class Kantor extends Container {

    private JPanel Main;
    private JLabel ActualTimeTxt;
    private JLabel ActualTimeVal;
    private JLabel LastTimeTxt;
    private JLabel LastTimeVal;
    private JButton Update;
    private JButton Exit;
    private JTextArea textArea1;
    private JPanel currencies_main;
    private JPanel currencies;
    private HashMap<String, CurrencyDetails> panels;
    private HashMap<String, Double> lastSellValues;


    Kantor() {
        panels = new HashMap<>();
        lastSellValues = new HashMap<>();
        JFrame frame = new JFrame("Kantor Helper 2.0");
        frame.setContentPane(this.Main);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        currencies.setLayout(new GridLayout(0, 2, 5, 5));
        updateTime();
        updateCurrency();
        Exit.addActionListener(e -> System.exit(0));
        Update.addActionListener(e -> updateCurrency());
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private void printToLog(String msg) {
        textArea1.insert(msg, 0);
    }

    private void updateTime() {
        Timer SimpleTimer = new Timer(1000, e -> {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            ActualTimeVal.setText(sdf.format(cal.getTime()));
            if (cal.get(Calendar.MINUTE) % 5 == 0 && cal.get(Calendar.SECOND) == 0) updateCurrency();
        });
        SimpleTimer.start();
    }

    private void updateCurrency() {

        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.centkantor.pl/kursy-walut")
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .timeout(3000)
                    .post();
        } catch (Exception e) {
            printToLog(e.toString());
            printToLog("[Problem z połaczeniem do serwera]");
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        LastTimeVal.setText(sdf.format(cal.getTime()));

        boolean change = false;
        String message = "[" + sdf.format(cal.getTime()) + "] \n";

        try {
            Element table = doc.getElementById("ex_table").select("tbody").get(0);
            Elements rows = table.select("tr");

            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                String currencyCode = cols.get(1).text();
                String currencyName = cols.get(2).text();
                String buyValue = cols.get(3).text();
                double bValue = Double.parseDouble(buyValue);
                String buyCentValue = String.format("%s", bValue);
                String buySuggestedValue = " ";
                String sellValue = cols.get(4).text();
                double sValue = Double.parseDouble(sellValue);
                String sellCentValue = String.format("%s", sValue);
                double calculatedSellValue = calculateValue(sValue);
                String sellSuggestedValue = String.format("%s", calculatedSellValue);
                if (panels.containsKey(currencyCode)) {
                    panels.get(currencyCode).updateValues(buyCentValue, sellCentValue, buySuggestedValue, sellSuggestedValue);
                    if (lastSellValues.get(currencyCode) != calculatedSellValue) {
                        change = true;
                        message += " " + currencyCode + " z " + lastSellValues.get(currencyCode) + " na " + calculatedSellValue + "\n";
                        lastSellValues.put(currencyCode, calculatedSellValue);
                    }
                } else {
                    CurrencyDetails panel = CurrencyDetails.createCurrencyDetails(currencyCode, currencyName, buyCentValue, sellCentValue, buySuggestedValue, sellSuggestedValue);
                    if (i % 2 == 1) {
                        panel.disableSeparator();
                    }
                    panels.put(currencyCode, panel);
                    currencies.add(panel.getDetailsPanel());
                    lastSellValues.put(currencyCode, calculatedSellValue);
                }
            }
        } catch (NullPointerException e) {
            printToLog(e.toString());
            printToLog("[Brak tabeli 'ex_table' na stronie]");
        } catch (Exception e) {
            printToLog(e.toString());
            printToLog("[Problem z przetworzeniem strony]");
        }

        //Create info
        if (change) {
            printToLog(message);

            JFrame frame = new JFrame();

            final JDialog dialog = new JDialog(frame,
                    "Wiadomość");

            JLabel label = new JLabel(message);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.PLAIN,
                    28.0f));

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> {
                dialog.setVisible(false);
                dialog.dispose();
            });
            JPanel closePanel = new JPanel();
            closePanel.setLayout(new BoxLayout(closePanel,
                    BoxLayout.LINE_AXIS));
            closePanel.add(Box.createHorizontalGlue());
            closePanel.add(closeButton);
            closePanel.setBorder(BorderFactory.
                    createEmptyBorder(0, 0, 5, 5));
            JPanel contentPane = new JPanel(new BorderLayout());
            contentPane.add(label, BorderLayout.CENTER);
            contentPane.add(closePanel, BorderLayout.PAGE_END);
            contentPane.setOpaque(true);
            dialog.setContentPane(contentPane);
            dialog.setAlwaysOnTop(true);
            //Show it.
            dialog.setSize(new Dimension(300, 150));
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            Main.requestFocus(true);
        }

    }

    private double calculateValue(double value) {
        if (value > 1.0) {
            return round((value + 0.03) * 100.0) / 100.0;
        }
        if (value < 1.0 && value >= 0.05) {
            return ceil(value * 100.0) / 100.0;
        }
        if (value < 0.05) {
            return ceil(value * 1000.0) / 1000.0;
        }
        return 0.0;
    }

}
