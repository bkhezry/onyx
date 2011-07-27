/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.jade.instrument.ndd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.obiba.onyx.jade.instrument.ExternalAppLauncherHelper;
import org.obiba.onyx.jade.instrument.InstrumentRunner;
import org.obiba.onyx.jade.instrument.service.InstrumentExecutionService;
import org.obiba.onyx.util.FileUtil;
import org.obiba.onyx.util.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Specified instrument runner for the ndd Spirometer.
 */
public class EasyOnPCInstrumentRunner implements InstrumentRunner, InitializingBean {

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(EasyOnPCInstrumentRunner.class);

  // Injected by spring.
  protected InstrumentExecutionService instrumentExecutionService;

  protected ExternalAppLauncherHelper externalAppHelper;

  private String dbPath;

  // participant data
  private String participantID;

  private String participantGender;

  private Double participantWeight;

  private Double participantHeight;

  public InstrumentExecutionService getInstrumentExecutionService() {
    return instrumentExecutionService;
  }

  public void setInstrumentExecutionService(InstrumentExecutionService instrumentExecutionService) {
    this.instrumentExecutionService = instrumentExecutionService;
  }

  public ExternalAppLauncherHelper getExternalAppHelper() {
    return externalAppHelper;
  }

  public void setExternalAppHelper(ExternalAppLauncherHelper externalAppHelper) {
    this.externalAppHelper = externalAppHelper;
  }

  public String getDbPath() {
    return dbPath;
  }

  public void setDbPath(String dbPath) {
    this.dbPath = dbPath;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    participantID = instrumentExecutionService.getParticipantID();
    participantGender = instrumentExecutionService.getInputParameterValue("INPUT_PARTICIPANT_GENDER").getValue();
    participantWeight = instrumentExecutionService.getInputParameterValue("INPUT_PARTICIPANT_WEIGHT").getValue();
    participantHeight = instrumentExecutionService.getInputParameterValue("INPUT_PARTICIPANT_HEIGHT").getValue();
  }

  /**
   * Retrieve participant data from the database and write them in the spirometer input file
   * @throws Exception
   */
  public void initParticipantData() {
    // TODO write XML file
  }

  /**
   * Initialise or restore instrument data (database and scan files).
   * @throws Exception
   */
  protected void resetDeviceData() {
    File backupDbFile = new File(getDbPath() + ".orig");
    File currentDbFile = new File(getDbPath());

    try {
      if(backupDbFile.exists()) {
        FileUtil.copyFile(backupDbFile, currentDbFile);
        backupDbFile.delete();
        // TODO delete XML files
      } else {
        // init
        FileUtil.copyFile(currentDbFile, backupDbFile);
      }
    } catch(Exception ex) {
      throw new RuntimeException("Error while reseting device data: ", ex);
    }
  }

  private List<Map<String, Data>> retrieveDeviceData() {

    List<Map<String, Data>> dataList = new ArrayList<Map<String, Data>>();

    return dataList;

  }

  public void sendDataToServer(Map<String, Data> data) {
    instrumentExecutionService.addOutputParameterValues(data);
  }

  /**
   * Implements parent method initialize from InstrumentRunner Delete results from previous measurement and initiate the
   * input file to be read by the external application
   */
  public void initialize() {
    log.info("Backup local database");
    resetDeviceData();

    log.info("Setting participant data");
    initParticipantData();
  }

  /**
   * Implements parent method run from InstrumentRunner Launch the external application, retrieve and send the data
   */
  public void run() {
    log.info("Launching Easy on-PC software");
    externalAppHelper.launch();

    log.info("Retrieving measurements");
    List<Map<String, Data>> dataList = retrieveDeviceData();

    log.info("Sending data to server");
    for(Map<String, Data> dataMap : dataList) {
      sendDataToServer(dataMap);
    }
  }

  /**
   * Implements parent method shutdown from InstrumentRunner Delete results from current measurement
   */
  public void shutdown() {
    log.info("Restoring local database and cleaning data files");
    resetDeviceData();
  }
}