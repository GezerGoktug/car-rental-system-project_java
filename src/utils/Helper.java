package utils;

import javax.swing.*;

public class Helper {
    // Arayüz teması için.
    public static void setTheme() {
        try {
            // Nimbus tema örneği
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
