package com.here.owc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emr_job_messages")
public class EmrJobMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="execution_id")
    private EmrJobExecution emrJobExecution;

    @Column(name = "message")
    private String message;


    public EmrJobMessage() {
    }

    public EmrJobMessage(EmrJobExecution emrJobExecution, String message) {
        this.emrJobExecution = emrJobExecution;
        this.message = message;
    }

    public EmrJobMessage(Long messageId, EmrJobExecution emrJobExecution, String message) {
        this.messageId = messageId;
        this.emrJobExecution = emrJobExecution;
        this.message = message;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public EmrJobExecution getEmrJobExecution() {
        return this.emrJobExecution;
    }

    public void setEmrJobExecution(EmrJobExecution emrJobExecution) {
        this.emrJobExecution = emrJobExecution;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("messageId", messageId)
                .append("emrJobExecution", emrJobExecution)
                .append("message", message)
                .toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmrJobMessage that = (EmrJobMessage) o;

        return new EqualsBuilder()
                .append(messageId, that.messageId)
                .append(emrJobExecution, that.emrJobExecution)
                .append(message, that.message)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(messageId)
                .append(emrJobExecution)
                .append(message)
                .toHashCode();
    }
}
