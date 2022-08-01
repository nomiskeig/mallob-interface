package edu.kit.fallob.mallobio.listeners.outputloglisteners;

import edu.kit.fallob.database.WarningDao;
import edu.kit.fallob.mallobio.outputupdates.Warning;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class WarningListener implements OutputLogLineListener {
	
	private WarningDao warningDao;
	
	public WarningListener(WarningDao dao) {
		warningDao = dao;
	}

	@Override
	public void processLine(String line) {
		if (Warning.isWarning(line)) {
			try {
				warningDao.save(new Warning(line));
			} catch (FallobException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
