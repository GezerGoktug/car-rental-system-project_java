import utils.Helper;
import utils.ServiceManager;
import view.LoginPage;

import javax.swing.*;

public class Main {
    /**
     * Admin mail: admin@gmail.com
     * Admin şifre: admin1234
     * <p>
     * Test kullanıcı email: test@gmail.com
     * Test kullanıcı şifre: test1234
     */

    public static void main(String[] args) {
        new ServiceManager();
        Helper.setTheme();
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}


