package ch.cevi.db.client.gui.logpane;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Appender LogBack for JTextArea (swing component)
 * 
 * Class copied from
 * https://github.com/jmarginier/simulator/blob/master/gui-simulator/src/main/
 * java/fr/ensicaen/gui_simulator/gui/core/JTextAreaAppender.java and adaped to
 * Cevi.DB Client.
 * 
 * @author Florent Moisson
 * 
 */
public class JTextAreaAppender extends AppenderBase<ILoggingEvent> {
	private static Logger logger = LoggerFactory.getLogger(JTextAreaAppender.class);

	private PatternLayoutEncoder encoder;
	private JTextArea textArea;

	public JTextAreaAppender(JTextArea textArea) {
		logger.info("Initialization of GUI Console...");
		this.textArea = textArea;
		encoder = new PatternLayoutEncoder();
		encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger{36} - %msg%n");
		encoder.setImmediateFlush(true);
		// set ctx & launch
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		encoder.setContext(lc);
		setContext(lc);
		start();

		// auto-add
		lc.getLogger("ROOT").addAppender(this);
	}

	@Override
	public void start() {
		encoder.start();
		super.start();
	}

	@Override
	public void stop() {
		encoder.stop();
		super.stop();
	}

	@Override
	public void append(ILoggingEvent event) {
		byte[] data = encoder.encode(event);
		String line = new String(data, StandardCharsets.UTF_8);
		textArea.append(line);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}

}