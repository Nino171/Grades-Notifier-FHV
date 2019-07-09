package Quellcode_URL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;

public class MainFrame extends Frame {
    private TextArea _quellCode;
    private TextArea _actionArea;
    private JEditorPane _pageDesign;
    private SourceCodeComperator _scc;
    private Thread _thread;
    private Panel _btnPanel;


    public MainFrame(){
        super("test");

        setSize(1200,1000);

        initialize();
        setVisible(true);
    }

    public void initialize(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());

        JPanel CodePanel = new JPanel();
        Panel ControlPanel = new Panel(new GridLayout(2,1));
        _pageDesign = new JEditorPane();

        _pageDesign.setContentType("text/html");
        _pageDesign.getDocument().putProperty("IgnoreCharsetDirective",Boolean.TRUE);
        JScrollPane scrollPane = new JScrollPane(_pageDesign);
        scrollPane.setPreferredSize(new Dimension(600,600));


        _quellCode = new TextArea();
        _quellCode.setEditable(false);
        _quellCode.setPreferredSize(new Dimension(600,600));
        CodePanel.add(_quellCode);

        Panel placeholder = new Panel();
        add(placeholder,BorderLayout.CENTER);

        add(scrollPane,BorderLayout.EAST);
        add(ControlPanel,BorderLayout.SOUTH);
        add(CodePanel,BorderLayout.WEST);

        Panel ActionPanel = new Panel();
        _actionArea= new TextArea();

        _actionArea.setEditable(false);
        _actionArea.setSize(200,100);

        ActionPanel.add(_actionArea);

        _btnPanel = new Panel();
        ControlPanel.add(ActionPanel);
        ControlPanel.add(_btnPanel);

        initControls();


        _scc = new SourceCodeComperator();
        _scc.setFrame(this); //gebe das frame hin damit dieser eine referenz besitzt


    }

    public void setQuellCode(String value){
        _quellCode.setText(value);
    }
    public void appendAktionText(String value){
        LocalDateTime ldt = LocalDateTime.now();
        _actionArea.append(ldt + ": "+ value + "\n");
    }
    public void setPageDesignText(String value){
            _pageDesign.setText(value);

    }

    private void initControls(){
        Label lblUser = new Label("Student-Email: ");
        Label lblPwd = new Label("PWD: ");

        TextField txtUser = new TextField(10);
        JPasswordField txtPwd = new JPasswordField(10);

        _btnPanel.add(lblUser);
        _btnPanel.add(txtUser);
        _btnPanel.add(lblPwd);
        _btnPanel.add(txtPwd);

        Button btnRead = new Button("Auslesen");
        _btnPanel.add(btnRead);

        Button btnStop = new Button("Stop");
        btnStop.setEnabled(false);
        _btnPanel.add(btnStop);


        btnRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _scc.setUsernameAndPassword(txtUser.getText(),txtPwd.getText());
                _thread = new Thread(_scc);
                _thread.start();
                appendAktionText("Thread started. -> Verbindung wird aufgebaut...");
                btnRead.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _thread.stop();
                _thread = null;
                appendAktionText("Thread stopped.");
                btnRead.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });


    }


}
