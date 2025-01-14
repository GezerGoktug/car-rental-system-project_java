package view;

import controller.AuthController;
import exceptions.RentalException;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfilePanel extends JFrame {
    private User currentUser;

    public ProfilePanel(User user) {
        this.currentUser = user;
        initializeFrame();
    }

    //Form başlatınca.
    private void initializeFrame() {
        //Ana çerçeve ayarları
        setTitle("Update Profile Panel");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //En üstte geri dön butonu için panel
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            CustomerDashboard mainFrame = new CustomerDashboard(currentUser);
            mainFrame.setVisible(true);
            dispose();
        });
        backButtonPanel.add(backButton);
        add(backButtonPanel, BorderLayout.NORTH);

        //Üst panel: Bilgi gösterimi
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 1));
        topPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

        topPanel.add(new JLabel("Role: " + currentUser.getRole()));
        topPanel.add(new JLabel("Email: " + currentUser.getEmail()));
        topPanel.add(new JLabel("Username: " + currentUser.getUsername()));
        topPanel.add(new JLabel("Registration Date: " + currentUser.getRegistrationDate()));

        add(topPanel, BorderLayout.CENTER);

        //Alt panel: Güncelleme formu
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.SOUTH);

        //Görünür yap
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    //Form paneli oluşturma
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Update Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //Username
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        addToPanel(formPanel, usernameLabel, usernameField, gbc, 0);


        //Password
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        addToPanel(formPanel, passwordLabel, passwordField, gbc, 2);


        //Update
        JButton submitButton = new JButton("Update Profile");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(submitButton, gbc);

        //Buton olay dinleyicisi
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    User user = AuthController.updateUserInfos(currentUser, username, password);
                    JOptionPane.showMessageDialog(null,
                            "Update Account Successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    currentUser = user;

                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null,
                            err.getMessage(),
                            "Update Account Error",
                            JOptionPane.ERROR_MESSAGE);
                }


            }
        });

        //Hesabı silme butonu
        JButton deleteButton = new JButton("Delete My Account");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(deleteButton, gbc);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete your account?",
                    "Delete Account Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {

                    AuthController.deleteUser(currentUser.getUserId());
                    new LoginPage().setVisible(true);
                    this.dispose();
                } catch (IllegalArgumentException | RentalException err) {
                    JOptionPane.showMessageDialog(this, err.getMessage(), "Delete Account Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        return formPanel;
    }

    //Yardımcı yöntem: Elemanları panele ekleme
    private void addToPanel(JPanel panel, JLabel label, JComponent field, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }


}
