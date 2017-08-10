package pt.uc.student.aclima.device_agent.Messaging.Publisher;

/**
 * Created by aclima on 22/07/2017.
 */

public class DeviceMessage {

    private String subtopic;
    private String deviceId;
    private String payload;

    DeviceMessage(String subtopic, String deviceId, String payload) {
        this.subtopic = subtopic;
        this.deviceId = deviceId;
        this.payload = payload;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
