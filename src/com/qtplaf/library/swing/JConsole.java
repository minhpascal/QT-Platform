/**
 * 
 */
package com.qtplaf.library.swing;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

/**
 * A console mapped to a text area.
 * 
 * @author Miquel Sas
 */
public class JConsole extends JTextArea {

	/**
	 * Output stream implementor.
	 */
	class JConsoleOutputStream extends OutputStream {

		/**
		 * Write a byte.
		 */
		public void write(int b) {
			append(new String(new byte[] { (byte) b }));
		}

		/**
		 * Write bytes from a source byte array.
		 */
		public void write(byte src[], int off, int len) {
			if (src == null) {
				throw new NullPointerException();
			} else if ((off < 0)
				|| (off > src.length)
				|| (len < 0)
				|| ((off + len) > src.length)
				|| ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
			byte[] dest = new byte[len];
			System.arraycopy(src, off, dest, 0, len);
			append(new String(dest));
		}
	}

	/**
	 * Maximum number of line, zero no limit, default is 1000.
	 */
	private int maxLines = 1000;
	/**
	 * This console print stream.
	 */
	private PrintStream printStream;

	/**
	 * Default constructor.
	 */
	public JConsole() {
		super();
	}

	/**
	 * Constructor assigning the maximum numbeer of lines.
	 * 
	 * @param maxLines The maximum number of lines for this console.
	 */
	public JConsole(int maxLines) {
		super();
		this.maxLines = maxLines;
	}

	/**
	 * Returns this console print stream.
	 * 
	 * @return This console print stream.
	 */
	public PrintStream getPrintStream() {
		if (printStream == null) {
			printStream = new PrintStream(new JConsoleOutputStream());
		}
		return printStream;
	}

	/**
	 * Append the string to the console.
	 * 
	 * @param str The string to print.
	 */
	public void append(String str) {
		super.append(str);
		if (maxLines > 0) {
			try {
				int lineCount = getLineCount();
				if (lineCount > (maxLines + maxLines / 2)) {
					int startLine = 0;
					int endLine = lineCount - maxLines;
					int startOffset = getLineStartOffset(startLine);
					int endOffset = getLineEndOffset(endLine);
					replaceRange("", startOffset, endOffset);
				}
				int startOffset = getLineStartOffset(getLineCount() - 1);
				setCaretPosition(startOffset);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Clear this text area console.
	 */
	public void clear() {
		setText("");
	}

	/**
	 * Get the maximum number of lines of this console.
	 * 
	 * @return The maxLines.
	 */
	public int getMaxLines() {
		return maxLines;
	}

	/**
	 * Set the maximum number of lines of this console.
	 * 
	 * @param maxLines The maxLines to set.
	 */
	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
	}

}
