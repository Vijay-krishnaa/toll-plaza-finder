
package com.tollfinder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "toll_plazas")
public class TollPlaza {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "toll_name")
  private String tollName;

  private Double latitude;

  private Double longitude;

  @Column(name = "geo_state")
  private String geoState;

  public TollPlaza() {
  }

  public TollPlaza(String tollName, Double latitude,
      Double longitude, String geoState) {
    this.tollName = tollName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.geoState = geoState;
  }

  public Long getId() {
    return id;
  }

  public String getTollName() {
    return tollName;
  }

  public void setTollName(String tollName) {
    this.tollName = tollName;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getGeoState() {
    return geoState;
  }

  public void setGeoState(String geoState) {
    this.geoState = geoState;
  }
}
