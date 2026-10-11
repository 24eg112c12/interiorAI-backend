package com.interiorai.service;
import com.interiorai.entity.RoomProject;
import org.springframework.stereotype.Service;
import java.math.*;import java.util.*;
@Service
public class CostEstimator {
 public record CostLine(String category,BigDecimal quantity,BigDecimal unitPrice) {}
 /** Heuristic illustrative values in INR, NOT supplier quotations or live local prices. */
 public List<CostLine> estimate(RoomProject p,String variant){
  BigDecimal sqFt=p.length.multiply(p.width).multiply(p.unit.equals("m")?new BigDecimal("10.7639"):BigDecimal.ONE);
  BigDecimal scale=sqFt.divide(new BigDecimal("160"),4,RoundingMode.HALF_UP).max(new BigDecimal("0.60")).min(new BigDecimal("2.50"));
  BigDecimal multiplier=switch(variant){case "Minimalist"->new BigDecimal("0.82");case "Luxury"->new BigDecimal("1.50");default->BigDecimal.ONE;};
  String major=p.roomType.equalsIgnoreCase("Bedroom")?"Bed & storage":p.roomType.equalsIgnoreCase("Kitchen")?"Cabinets":p.roomType.equalsIgnoreCase("Bathroom")?"Fixtures":p.roomType.equalsIgnoreCase("Office")?"Desk & storage":"Sofa";
  List<CostLine> list=new ArrayList<>();
  add(list,major,45000,scale,multiplier);add(list,"False ceiling",35000,scale,multiplier);add(list,"Wall painting",20000,scale,multiplier);
  add(list,"Lighting",15000,scale,multiplier);add(list,"Storage / TV unit",40000,scale,multiplier);add(list,"Curtains / blinds",15000,scale,multiplier);add(list,"Labour & installation",20000,scale,multiplier);
  return list;
 }
 private void add(List<CostLine> list,String label,int amount,BigDecimal area,BigDecimal style){list.add(new CostLine(label,BigDecimal.ONE,new BigDecimal(amount).multiply(area).multiply(style).setScale(0,RoundingMode.HALF_UP)));}
 public static BigDecimal total(Collection<CostLine> items){return items.stream().map(i->i.quantity.multiply(i.unitPrice)).reduce(BigDecimal.ZERO,BigDecimal::add);}
}
