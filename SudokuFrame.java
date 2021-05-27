import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class SudokuFrame extends JFrame {

 	private JTextArea puzzleTextArea;
 	private JTextArea solutionTextArea;
 	private JCheckBox autoCheck;

	public SudokuFrame() {
		super("Sudoku Solver");
		
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
		 JPanel pane = new JPanel(new BorderLayout(4,4));
		 puzzleTextArea = new JTextArea(15,20);
		 puzzleTextArea.setBorder(new TitledBorder("Puzzle"));
		 puzzleTextArea.addKeyListener(new KeyListener() {
			 @Override
			 public void keyTyped(KeyEvent keyEvent) {
			 	if (autoCheck.isSelected())
			 		calculateSudokuSolution();
			 }

			 @Override
			 public void keyPressed(KeyEvent keyEvent) {

			 }

			 @Override
			 public void keyReleased(KeyEvent keyEvent) {

			 }
		 });
		 pane.add(puzzleTextArea, BorderLayout.WEST);
		 solutionTextArea = new JTextArea(15,20);
		 solutionTextArea.setBorder(new TitledBorder("Solution"));
		 pane.add(solutionTextArea, BorderLayout.EAST);
		 var subPane = new JPanel(new BorderLayout());
		 var checkButton = new JButton("Check");
		 subPane.add(checkButton, BorderLayout.WEST);
		 checkButton.addActionListener(actionEvent -> {
			 calculateSudokuSolution();
		 });
		 autoCheck = new JCheckBox("Auto Check");
		 subPane.add(autoCheck, BorderLayout.CENTER);
		 pane.add(subPane, BorderLayout.SOUTH);
		 this.add(pane);
	 }

	 private void calculateSudokuSolution() {
		 try {
			 var textList = Arrays.stream(puzzleTextArea.getText()
					 .split(","))
					 .map(o -> o.replace("\"\n", ""))
					 .collect(Collectors.toList());
			 var textArr = new String[textList.size()];
			 var sudoku = new Sudoku(Sudoku.stringsToGrid(textList.toArray(textArr)));
			 var result = sudoku.solve();
			 var displayText = String.format("%ssolutions:%d\nelapsed%dms", sudoku.getSolutionText(), result, sudoku.getElapsed());
			 solutionTextArea.setText(displayText);
		 }
		 catch (Exception ex) {
		 	solutionTextArea.setText("parsing problem");
		 }
	 }

	 public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
