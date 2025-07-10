
package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomePage extends JFrame {

    private  final CardLayout cardLayout;
    private  final JPanel contentPanel;

    public WelcomePage() {
        setTitle("TrackWell App");
        setSize(800, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel homePanel = createHomePanel();
        JPanel aboutPanel = createAboutPanel();
        JPanel helpPanel = createHelpPanel();

        contentPanel.add(homePanel, "Home");
        contentPanel.add(aboutPanel, "About");
        contentPanel.add(helpPanel, "Help");

        setJMenuBar(createMenuBar());
        add(contentPanel);
        cardLayout.show(contentPanel, "Home");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {

                cardLayout.show(contentPanel, "Home");
            }
        });

        JMenuItem aboutItem = new JMenuItem("About us");
        aboutItem.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "About");
            }
        });

        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {

                cardLayout.show(contentPanel, "Help");
            }
        });

        menuBar.add(homeItem);
        menuBar.add(aboutItem);
        menuBar.add(helpItem);

        return menuBar;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        try {
            ImageIcon logoIcon = new ImageIcon((getClass().getResource("logo1.png")));
            Image scaledImage = logoIcon.getImage().getScaledInstance(800, 600, Image.SCALE_SMOOTH);
            JLabel background = new JLabel(new ImageIcon(scaledImage));
            background.setLayout(new OverlayLayout(background));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 290));
            buttonPanel.setOpaque(false);
            buttonPanel.setAlignmentX(0.5f);
            buttonPanel.setAlignmentY(0.5f);

            buttonPanel.setBackground(new Color(125, 168, 221));
            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LoginFrame log = new LoginFrame();
                    log.setVisible(true);
                }
            });

            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RegistrationFrame reg = new RegistrationFrame();
                    reg.setVisible(true);
                }
            });

            buttonPanel.add(Box.createVerticalGlue());
            buttonPanel.add(loginButton);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(registerButton);
            buttonPanel.add(Box.createVerticalGlue());

            background.add(buttonPanel);

            panel.add(background, BorderLayout.CENTER);
        } catch (Exception e) {
            panel.setBackground(new Color(125, 168, 221));
            JLabel errorLabel = new JLabel("Logo not found");
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(125, 168, 221));

        // Load the logo image
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("logo.png"));
            Image logoImage = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(logoLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String description = "<html><div style='text-align: justify; font-family: Times New Roman; font-size: 12px; color: #003366; width: 600px;'>"
                + "The TrackWell application is a health tracker system built using Java and Swing, designed to help users manage "
                + "their health-related data through a graphical user interface (GUI). The main interface consists of a welcoming " + "screen with options to sign in or log in, using buttons that trigger different actions such as opening the sign-up "
                + "and login pages. The application uses standardized background images for each page, which creates a consistent feel and "
                + "look throughout the interface to enhance the visual appeal.<br><br>"
                + "To use the application, users need to create an account by clicking the sign-up button, where they can enter their credentials, "
                + "email, and password. After successfully signing up or logging in, users are presented with the service page, where "
                + "they can access features like adding records, viewing saved records, and making payments. The record-keeping "
                + "functionality allows users to input their health data such as age, weight, and activities, and save it for later review.<br><br>"
                + "The view record page displays the saved information in a non-editable format, ensuring that the data remains "
                + "secure. The application also includes a payment page, where users can enter the amount they wish to pay and "
                + "choose from various payment options like Visa, MasterCard, Apple Pay, or PayPal. Throughout the app, the "
                + "interface is designed to be user-friendly, with intuitive navigation and clear labeling to guide users through the "
                + "various functions and features. Each action is confirmed with a pop-up message to reassure the user, such as "
                + "notifying them when their account is created successfully or when a record is saved. The program is built with a "
                + "modular approach, making it easy to manage the various pages."
                + "</div></html>";

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setVerticalAlignment(SwingConstants.CENTER);

        descriptionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        descriptionLabel.setForeground(new Color(0, 51, 102));

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setBackground(new Color(125, 168, 221));
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.add(descriptionLabel, BorderLayout.CENTER);

        panel.add(descriptionPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(125, 168, 221));

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("logo.png"));
            Image logoImage = logoIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(logoLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String description = "For help contact us >> support@trackwell.com";
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setVerticalAlignment(SwingConstants.CENTER);

        descriptionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        descriptionLabel.setForeground(new Color(0, 51, 102));

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setBackground(new Color(125, 168, 221));
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.add(descriptionLabel, BorderLayout.CENTER);

        panel.add(descriptionPanel, BorderLayout.CENTER);
        return panel;
    }

}
