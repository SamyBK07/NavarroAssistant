<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#000000"
    tools:context=".MainActivity">

    <!-- Barre de progression pour le téléchargement du modèle VOSK -->
    <ProgressBar
        android:id="@+id/progressBar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:visibility="visible" <!-- Visible au démarrage -->
        android:indeterminate="false"
        android:max="100"
        android:progress="0"
        android:progressTint="#00FF00" />

    <!-- Horloge analogique fluorescente (masquée au démarrage) -->
    <com.navarro.custom.ClockView
        android:id="@+id/clockView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:layout_centerInParent="true"
        android:visibility="gone" />

    <!-- Zone de texte pour afficher les réponses de l'assistant -->
    <TextView
        android:id="@+id/responseText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="32dp"
        android:visibility="gone"
        android:background="#33000000"
        android:padding="16dp"
        android:textColor="#00FF00"
        android:textSize="18sp"
        android:textStyle="bold" />

</RelativeLayout>
