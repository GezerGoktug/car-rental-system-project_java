package view;

import controller.AuthController;
import models.User;
import repositories.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;

    public LoginPage() {
        setTitle("Car Rental - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Ana Panel oluşturma
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        //Eposta alanı için label ve textfield oluşturma
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(emailField, gbc);

        //Şifre alanı için label ve textfield oluşturma
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);

        //Giriş Butonu
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);

        //Kaydol Butonu
        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(registerButton, gbc);


        //Giriş Butonuna çalışan komut
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        //Kaydol Butonuna çalışan komut
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterPage();
            }
        });


        add(mainPanel);
    }

    //login Metodu
    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            //Control işlemi yapıyor
            User user = AuthController.login(email, password);
            if (user.hasAdminAccess()) {
                new AdminDashboard().setVisible(true);
                dispose();
            } else {
                new CustomerDashboard(user);
                dispose();
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(this,
                    err.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }


    }

    //Register formuna geçiş
    private void openRegisterPage() {
        new RegisterPage().setVisible(true);
        dispose();
    }


}
