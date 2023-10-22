package telran.monitoring.service;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.monitoring.dto.PulseProbe;
@Service
@Slf4j
public class PulseProbesImitatorImpl implements PulseProbesImitator {
	@Value("${app.probes.imitator.value.min}")
	int minValue;
	@Value("${app.probes.imitator.value.max}")
	int maxValue;
	@Value("${app.probes.imitator.jump.multiplier}")
	double jumpMultiplier;
	@Value("${app.probes.imitator.nojump.max.multiplier}")
	double noJumpMaxMultiplier;
	@Value("${app.probes.imitator.jump.prob}")
	int jumpProb;
	@Value("${app.probes.imitator.increase.prob}")
	int increaseProb;
	@Value("${app.probes.imitator.patients.amount}")
	int nPatients;
	HashMap<Long, Integer> valuesMap = new HashMap<>();
	int seqNumber = 0;
	@Override
	public PulseProbe nextProbe() {
		
		long patientId = getPatientId();
		int value = getAndUpdateValue(patientId);
		return new PulseProbe(patientId, value, System.currentTimeMillis(), ++seqNumber);
	}
	private int getAndUpdateValue(long patientId) {
		int oldValue = valuesMap.getOrDefault(patientId, 0);
		int newValue = oldValue == 0 ? (int) getRandomLong(minValue, maxValue + 1) : getNewValue(oldValue);
		valuesMap.put(patientId, newValue);
		
		return newValue;
	}
	private int getNewValue(int oldValue) {
		boolean isJump = chance(jumpProb);
		int newValue = isJump ? jumpValue(oldValue) : noJumpValue(oldValue);
		log.trace("old value: {}, new Value: {}, isJump: {}", oldValue, newValue, isJump);
		return newValue;
	}
	private int noJumpValue(int oldValue) {
		double delta = oldValue * getDoubleRandom(0.0, noJumpMaxMultiplier);
		int res = getValueDelta(oldValue, delta);
		return res;
	}
	private double getDoubleRandom(double min, double max) {
		
		return ThreadLocalRandom.current().nextDouble(min, max);
	}
	private int jumpValue(int oldValue) {
		double delta = oldValue * jumpMultiplier;
		int res = getValueDelta(oldValue, delta);
		return res;
	}
	private int getValueDelta(int oldValue, double delta) {
		boolean isIncrease = chance(increaseProb);
		if(!isIncrease) {
			delta = -delta;
		}
		return (int) Math.round(oldValue + delta);
	}
	private boolean chance(int prob) {
		
		return getRandomLong(0, 100) < prob;
	}
	private long getPatientId() {
		
		return getRandomLong(1, nPatients + 1);
	}
	private long getRandomLong(int min, int max) {
		
		return ThreadLocalRandom.current().nextLong(min, max);
	}

}
