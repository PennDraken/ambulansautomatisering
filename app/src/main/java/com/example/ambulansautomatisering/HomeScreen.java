package com.example.ambulansautomatisering;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Lägg till en knapp för att simulera ett meddelande
        Button simulateMessageButton = findViewById(R.id.simulateMessageButton);
        simulateMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulera att du har fått ett meddelande
                showConfirmationDialog();
            }
        });
    }

    // Variabel för att hålla dialogobjektet globalt
    private AlertDialog confirmationDialog;

    // Lägg till denna metod i din MainActivity klass för att visa popup-rutan
    private void showConfirmationDialog() {
        // Skapa en AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Ställ in titel och meddelande
        builder.setTitle("Kvittera uppdrag");
        builder.setMessage("Vill du kvittera uppdraget?");

        // Lägg till knapp för att acceptera (Ja)
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Här kan du lägga till kod för att hantera kvitteringen av uppdraget
                // Exempel: visa en toast, spara kvittens i databasen, etc.

                // send "kvittens", update UI
                startActivity(new Intent(HomeScreen.this, MainActivity.class));
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
