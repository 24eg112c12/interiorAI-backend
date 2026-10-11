package com.interiorai.service;
import com.interiorai.entity.RoomProject;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class CostEstimatorTest {
 @Test void realisticDemoBaselineAndVariations(){RoomProject p=new RoomProject();p.roomType="Living Room";p.length=new BigDecimal("16");p.width=new BigDecimal("10");p.unit="ft";CostEstimator estimator=new CostEstimator();var basic=estimator.estimate(p,"Modern");assertEquals(0,new BigDecimal("190000").compareTo(CostEstimator.total(basic)));assertTrue(CostEstimator.total(estimator.estimate(p,"Minimalist")).compareTo(CostEstimator.total(basic))<0);assertTrue(CostEstimator.total(estimator.estimate(p,"Luxury")).compareTo(CostEstimator.total(basic))>0);}
 @Test void metricConversionAffectsEstimate(){RoomProject p=new RoomProject();p.roomType="Office";p.length=new BigDecimal("4");p.width=new BigDecimal("4");p.unit="m";assertTrue(CostEstimator.total(new CostEstimator().estimate(p,"Modern")).compareTo(new BigDecimal("190000"))>0);}
}
