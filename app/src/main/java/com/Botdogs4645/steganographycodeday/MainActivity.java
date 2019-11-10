package com.Botdogs4645.steganographycodeday;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.Math;


public class MainActivity extends AppCompatActivity {
    private int intentRequestCode = 1;
    private TextView mTextMessage;
    private ImageView imageEncryptIcon;
    private ImageView newimage;
    private Button encryptButton;
    private Button decryptButton;
    private EditText secretMessage;
    private ImageView imageDecryptIcon;


    private String payload;
    private Bitmap bitmap;
    private Bitmap newbitmap;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_encrypt:
                    mTextMessage.setText(R.string.title_encrypt);
                    imageEncryptIcon.setVisibility(View.VISIBLE);
                    imageDecryptIcon.setVisibility(View.INVISIBLE);
                    encryptButton.setVisibility(View.VISIBLE);
                    decryptButton.setVisibility(View.INVISIBLE);

                    return true;
                case R.id.navigation_decrypt:
                    mTextMessage.setText(R.string.title_decrypt);
                    imageEncryptIcon.setVisibility(View.INVISIBLE);
                    imageDecryptIcon.setVisibility(View.VISIBLE);
                    encryptButton.setVisibility(View.INVISIBLE);
                    decryptButton.setVisibility(View.VISIBLE);

                    return true;

            }
            return false;
        }
    };


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        imageEncryptIcon = (ImageView) findViewById(R.id.encryptionImageSelector);
        imageDecryptIcon = (ImageView) findViewById(R.id.decryptionImageSelector);
        encryptButton = (Button) findViewById(R.id.encryptButton);
        decryptButton = (Button) findViewById(R.id.decryptButton);
        secretMessage = (EditText) findViewById(R.id.secretMessage);
        newimage = (ImageView) findViewById(R.id.newimage);


        imageEncryptIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Select An Image From Gallery", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), intentRequestCode);
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    Toast.makeText(v.getContext(), "Encoding, Please Wait A Moment...", Toast.LENGTH_SHORT).show();
                    payload = secretMessage.getText().toString();
                    char[] letters = payload.toCharArray();
                    int pixels[] = new int[bitmap.getWidth() * bitmap.getHeight()];
                    int pix = 0;
                    ArrayList<Integer> color = new ArrayList<Integer>();


                    int letter = 0;
                    while (letter < letters.length) {

                        color.add((int) letters[letter]);
                        if (letter + 1 == letters.length) {
                            for (int i = color.size(); i < 3; i++) {
                                color.add(0);
                            }
                        }
                        if (color.size() >= 3) {
                            pixels[pix] = ((color.get(0) & 0x0ff) << 16) |
                                    ((color.get(1) & 0x0ff) << 8) |
                                    (color.get(2) & 0x0ff);

                            color.clear();


                        }
                        letter++;

                    }
                    for (int i = 0; i < pixels.length; i++) {
                        if (pixels[i] == 0) {
                            pixels[i] = bitmap.getPixel(i % bitmap.getWidth(), (int) Math.floor(i / bitmap.getWidth()));
                        }
                    }
                    newbitmap = bitmap.copy(bitmap.getConfig(), true);;
                    newbitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    newimage.setImageBitmap(newbitmap);
                    newimage.setBackground(null);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageEncryptIcon.setImageBitmap(bitmap);
                imageEncryptIcon.setBackground(null);
                // You can manipulate this bitmap aka inject your message into it! Just save it as a class variable to keep track

            } catch (IOException e) {
                Log.e("MainActivity", e.getMessage());
            }
        }
    }
}
