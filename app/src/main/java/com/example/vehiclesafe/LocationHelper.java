package com.example.vehiclesafe;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper {

    private LocationManager locationManager;
    private MyLocationListener locationListener;

    public LocationHelper(LocationManager locationManager) {
        this.locationManager = locationManager;
        this.locationListener = new MyLocationListener();
    }

    public String getCurrentLocationLink() {
        try {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            // Wait for a moment to receive location updates
            Thread.sleep(5000);
            // Stop receiving location updates
            locationManager.removeUpdates(locationListener);
            // Get the last known location
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                // Generate Google Maps link
                return getGoogleMapsLink(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return "Unable to retrieve current location";
    }

    private String getGoogleMapsLink(double latitude, double longitude) {
        return "https://www.google.com/maps?q=" + latitude + "," + longitude;
    }

    // Inner LocationListener class
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Handle location change if needed
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Handle status change if needed
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Handle provider enabled if needed
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Handle provider disabled if needed
        }
    }
}

