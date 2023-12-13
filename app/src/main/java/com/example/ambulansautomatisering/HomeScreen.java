package com.example.ambulansautomatisering;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeScreen extends AppCompatActivity {
    // Used to store our different completed missions
    private static List<TimeStampManager> completedMissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Schedule the popup to be shown after a delay (e.g., 5000 milliseconds or 5 seconds)
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::showConfirmationDialog, 1000); // TODO
    }

    // Adds a completed mission to the homescreen
    public void saveMission(TimeStampManager mission) {
        completedMissions.add(mission);
        // update UI
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    TimeStampManager completedMission = data.getParcelableExtra("MissionData");
                    saveMission(completedMission);
                }
            }
        }
    }

    // Shows completed missions in the UI
    private void updateUI() {
        for (TimeStampManager mission : completedMissions) {
            if (mission!=null) {
                // Get linearLayout which we will be adding our buttons too
                LinearLayout layout = (LinearLayout) findViewById(R.id.missionsLayout);
                // Sets the properties for each button
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText(mission.toTitleString());
                // Shows the button in the UI
                layout.addView(btnTag);
            }
        }
    }

    // Variabel för att hålla dialogobjektet globalt
    private AlertDialog confirmationDialog;

    // Lägg till denna metod i din MainActivity klass för att visa popup-rutan
    private void showConfirmationDialog() {
        // Skapa en AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Ställ in titel och meddelande
        builder.setTitle("Nytt uppdrag");
        builder.setMessage("Person har ramlat utanför Ica Kvantum Munkebäck. \nVill du acceptera uppdraget?");

        // Lägg till knapp för att acceptera (Ja)
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Här kan du lägga till kod för att hantera kvitteringen av uppdraget
                // Exempel: visa en toast, spara kvittens i databasen, etc.

                // send "kvittens", update UI
                startActivityForResult(new Intent(HomeScreen.this, MainActivity.class),1);
            }
        });

        // Lägg till knapp för att avbryta (Nej)
        builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Stäng befintlig dialog
                dialog.dismiss();

                // Skapa en ny dialog med uppdaterad text och en OK-knapp
                AlertDialog.Builder updatedBuilder = new AlertDialog.Builder(HomeScreen.this);
                updatedBuilder.setTitle("Uppdrag nekat");
                updatedBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Klick på OK, stäng popup-rutan
                        dialog.dismiss();
                    }
                });
                // Spara den uppdaterade dialogen globalt
                confirmationDialog = updatedBuilder.create();

                // Visa den uppdaterade dialogen
                confirmationDialog.show();
            }
        });

        // Skapa och visa AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Använd denna metod för att visa popup-rutan när du får ett meddelande
    protected void showMessageReceived() {
        // Implementera logiken här för att hantera ett mottaget meddelande
        // Till exempel: visa en toast, uppdatera UI, etc.
    }
}
