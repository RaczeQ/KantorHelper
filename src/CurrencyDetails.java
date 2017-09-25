import javax.swing.*;
import java.awt.*;

/**
 * Created by RaczeQ on 25/09/2017.
 */
public class CurrencyDetails extends JFrame {
    private JLabel CurrencyCode;
    private JLabel CurrencyName;
    private JLabel BuyCentValue;
    private JLabel BuySuggestedValue;
    private JLabel SellCentValue;
    private JLabel SellSuggestedValue;
    private JPanel detailsPanel;
    private JSeparator endSeparator;

    private CurrencyDetails(String currencyCode, String currencyName, String bcv, String scv, String bsv, String ssv) {
        this.setContentPane(detailsPanel);
        CurrencyCode.setText(currencyCode);
        CurrencyName.setText(currencyName);
        BuyCentValue.setText(bcv);
        SellCentValue.setText(scv);
        BuySuggestedValue.setText(bsv);
        SellSuggestedValue.setText(ssv);
    }

    public void updateData(String currencyCode, String currencyName, String bcv, String scv, String bsv, String ssv) {
        CurrencyCode.setText(currencyCode);
        CurrencyName.setText(currencyName);
        BuyCentValue.setText(bcv);
        SellCentValue.setText(scv);
        BuySuggestedValue.setText(bsv);
        SellSuggestedValue.setText(ssv);
    }

    void disableSeparator() {
        endSeparator.setVisible(false);
    }

    void updateValues(String bcv, String scv, String bsv, String ssv) {
        BuyCentValue.setText(bcv);
        SellCentValue.setText(scv);
        BuySuggestedValue.setText(bsv);
        SellSuggestedValue.setText(ssv);
    }

    JPanel getDetailsPanel() {
        return detailsPanel;
    }

    static CurrencyDetails createCurrencyDetails(String currencyCode, String currencyName, String bcv, String scv, String bsv, String ssv) {
        return new CurrencyDetails(currencyCode, currencyName, bcv, scv, bsv, ssv);
    }

}
