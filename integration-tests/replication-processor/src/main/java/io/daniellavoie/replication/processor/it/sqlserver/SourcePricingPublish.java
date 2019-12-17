package io.daniellavoie.replication.processor.it.sqlserver;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SourcePricingPublish {
	@Id
	private String keyEMDPricing;
	
	@Column(name = "KeyInstrument")
	private String keyInstrument;
	
	@Column(name = "InstrumentValueAsOf")
	private LocalDateTime instrumentValueAsOf;
	
	@Column(name = "KeyInstrumentSource")
	private int keyInstrumentSource;
	
	@Column(name = "KeyInstrumentLocation")
	private int keyInstrumentLocation;
	
	@Column(name = "KeyInstrumentMeasure")
	private int keyInstrumentMeasure;
	
	@Column(name = "InstrumentValue")
	private double instrumentValue;
	
	@Column(name = "InstrumentDeliveryStart")
	private LocalDateTime instrumentDeliveryStart;
	
	@Column(name = "InstrumentDeliveryEnd")
	private LocalDateTime instrumentDeliveryEnd;
	
	@Column(name = "UpdOperation")
	private int updOperation;
	
	@Column(name = "UpdDate")
	private LocalDateTime updDate;
	
	@Column(name = "ActiveRow")
	private boolean activeRow;
	
	@Column(name = "InstrumentValuePublishDate")
	private LocalDateTime instrumentValuePublishDate;

	public SourcePricingPublish() {

	}

	public SourcePricingPublish(String keyEMDPricing, String keyInstrument, LocalDateTime instrumentValueAsOf,
			int keyInstrumentSource, int keyInstrumentLocation, int keyInstrumentMeasure, double insturmentValue,
			LocalDateTime instrumentDeliveryStart, LocalDateTime instrumentDeliveryEnd, int updOperation,
			LocalDateTime updDate, boolean activeRow, LocalDateTime instrumentValuePublishDate) {
		this.keyEMDPricing = keyEMDPricing;
		this.keyInstrument = keyInstrument;
		this.instrumentValueAsOf = instrumentValueAsOf;
		this.keyInstrumentSource = keyInstrumentSource;
		this.keyInstrumentLocation = keyInstrumentLocation;
		this.keyInstrumentMeasure = keyInstrumentMeasure;
		this.instrumentValue = insturmentValue;
		this.instrumentDeliveryStart = instrumentDeliveryStart;
		this.instrumentDeliveryEnd = instrumentDeliveryEnd;
		this.updOperation = updOperation;
		this.updDate = updDate;
		this.activeRow = activeRow;
		this.instrumentValuePublishDate = instrumentValuePublishDate;
	}

	public String getKeyEMDPricing() {
		return keyEMDPricing;
	}

	public void setKeyEMDPricing(String keyEMDPricing) {
		this.keyEMDPricing = keyEMDPricing;
	}

	public String getKeyInstrument() {
		return keyInstrument;
	}

	public void setKeyInstrument(String keyInstrument) {
		this.keyInstrument = keyInstrument;
	}

	public LocalDateTime getInstrumentValueAsOf() {
		return instrumentValueAsOf;
	}

	public void setInstrumentValueAsOf(LocalDateTime instrumentValueAsOf) {
		this.instrumentValueAsOf = instrumentValueAsOf;
	}

	public int getKeyInstrumentSource() {
		return keyInstrumentSource;
	}

	public void setKeyInstrumentSource(int keyInstrumentSource) {
		this.keyInstrumentSource = keyInstrumentSource;
	}

	public int getKeyInstrumentLocation() {
		return keyInstrumentLocation;
	}

	public void setKeyInstrumentLocation(int keyInstrumentLocation) {
		this.keyInstrumentLocation = keyInstrumentLocation;
	}

	public int getKeyInstrumentMeasure() {
		return keyInstrumentMeasure;
	}

	public void setKeyInstrumentMeasure(int keyInstrumentMeasure) {
		this.keyInstrumentMeasure = keyInstrumentMeasure;
	}

	public double getInstrumentValue() {
		return instrumentValue;
	}

	public void setInstrumentValue(double insturmentValue) {
		this.instrumentValue = insturmentValue;
	}

	public LocalDateTime getInstrumentDeliveryStart() {
		return instrumentDeliveryStart;
	}

	public void setInstrumentDeliveryStart(LocalDateTime instrumentDeliveryStart) {
		this.instrumentDeliveryStart = instrumentDeliveryStart;
	}

	public LocalDateTime getInstrumentDeliveryEnd() {
		return instrumentDeliveryEnd;
	}

	public void setInstrumentDeliveryEnd(LocalDateTime instrumentDeliveryEnd) {
		this.instrumentDeliveryEnd = instrumentDeliveryEnd;
	}

	public int getUpdOperation() {
		return updOperation;
	}

	public void setUpdOperation(int updOperation) {
		this.updOperation = updOperation;
	}

	public LocalDateTime getUpdDate() {
		return updDate;
	}

	public void setUpdDate(LocalDateTime updDate) {
		this.updDate = updDate;
	}

	public boolean isActiveRow() {
		return activeRow;
	}

	public void setActiveRow(boolean activeRow) {
		this.activeRow = activeRow;
	}

	public LocalDateTime getInstrumentValuePublishDate() {
		return instrumentValuePublishDate;
	}

	public void setInstrumentValuePublishDate(LocalDateTime instrumentValuePublishDate) {
		this.instrumentValuePublishDate = instrumentValuePublishDate;
	}
}
