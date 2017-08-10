package pt.uc.student.aclima.device_agent.Messaging.Updater;

/**
 * Created by aclima on 10/08/2017.
 */

public class ConfigurationMessage {

    private String configurationName;
    private String configurationValue;

    public ConfigurationMessage(String configurationName, String configurationValue) {
        this.configurationName = configurationName;
        this.configurationValue = configurationValue;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getConfigurationValue() {
        return configurationValue;
    }

    public void setConfigurationValue(String configurationValue) {
        this.configurationValue = configurationValue;
    }
}
