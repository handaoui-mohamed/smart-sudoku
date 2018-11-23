package com.handaomo.smartsudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Grille extends View {

    private int screenWidth;
    private int screenHeight;
    private int n;

    private Paint paint1;   // Pour dessiner la grille (lignes noires)
    private Paint paint2;   // Pour le texte des cases fixes
    private Paint paint3;   // Pour dessiner les lignes rouges (grosse)
    private Paint paint4;   // Pour le texte noir des cases a modifier

    private int[][] matrix = new int[9][9];
    private boolean[][] fixIdx = new boolean[9][9];

    public Grille(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Grille(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Grille(Context context) {
        super(context);
        init();
    }

    private void init() {
        //Grille de depart
        set("000105000140000670080002400063070010900000003010090520007200080026000035000409000");

        // Grille Gagnante
        //set("672145398145983672389762451263574819958621743714398526597236184426817935831459267");

        // Grille Perdante
        //set("672145198145983672389762451263574819958621743714398526597236184426817935831459267");

        paint1 = new Paint();
        paint1.setAntiAlias(true);
        // Couleur noire

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        // Couleur rouge
        // Taille du texte
        // Centre le texte

        paint3 = new Paint();
        paint3.setAntiAlias(true);
        // Couleur rouge et grosses lignes

        // Paint 4 ?
    }

    @Override
    protected void onDraw(Canvas canvas) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        int w = Math.min(screenWidth, screenHeight);
        w = w - (w % 9);
        n = w / 9;

        int spacing = 10;

        for (int i = 0; i < w; i+=n) {
            for (int j = 0; j < w; j+=n) {
                // horizontal
                canvas.drawLine(j + spacing, i + spacing, j + n, i + spacing, paint1);
                canvas.drawLine(j + spacing, i + n , j + n, i + n, paint1);

                // vertical
                canvas.drawLine(j + spacing, i + spacing, j + spacing, i + n, paint1);
                canvas.drawLine(j + n , i + spacing,  j + n ,i + n, paint1);
            }
        }

        // Dessiner 2 lignes rouges verticales et 2 lignes rouges horizontales
        paint3.setColor(getResources().getColor(R.color.red));
        paint3.setStrokeWidth(spacing);
        canvas.drawLine(spacing, n * 3 + (spacing / 2), w, n * 3 + (spacing / 2), paint3);
        canvas.drawLine(spacing, n * 6 + (spacing / 2), w, n * 6 + (spacing / 2), paint3);

        canvas.drawLine(n * 3 + (spacing / 2), spacing, n * 3 + (spacing / 2), w, paint3);
        canvas.drawLine(n * 6 + (spacing / 2), spacing, n * 6 + (spacing / 2), w, paint3);


        final float textSize = 24f;
        paint1.setTextSize(textSize);
        paint2.setTextSize(textSize);
        // Les contenus des cases
        String s;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                s = "" + (matrix[j][i] == 0 ? "" : matrix[j][i]);
                if (fixIdx[j][i])
                    canvas.drawText(s, i * n + (n / 2) - (n / 10) + spacing, j * n
                            + (n / 2) + (n / 10), paint2);
                else
                    canvas.drawText(s, i * n + (n / 2) - (n / 10) + spacing, j * n
                            + (n / 2) + (n / 10), paint1);
            }
        }
    }

    public int getXFromMatrix(int x) {
        // Renvoie l'indice d'une case a partir du pixel x de sa position h
        return (x / n);
    }

    public int getYFromMatrix(int y) {
        // Renvoie l'indice d'une case a partir du pixel y de sa position v
        return (y / n);
    }

    public void set(String s, int i) {
        // Remplir la ieme ligne de la matrice matrix avec un vecteur String s
        int v;
        for (int j = 0; j < 9; j++) {
            v = s.charAt(j) - '0';
            matrix[i][j] = v;
            if (v == 0)
                fixIdx[i][j] = false;
            else
                fixIdx[i][j] = true;
        }
    }

    public void set(String s) {
        // Remplir la matrice matrix a partir d'un vecteur String s
        for (int i = 0; i < 9; i++) {
            set(s.substring(i * 9, i * 9 + 9), i);
        }
    }

    public void set(int x, int y, int v) {
        // Affecter la valeur v a la case (y, x)
        // y : ligne
        // x : colonne
    }

    public boolean isNotFix(int x, int y) {
        // Renvoie si la case (y, x) n'est pas fixe
        // A completer
        return false;
    }

    public boolean gagne() {
        // Verifier si la case n'est pas vide ou bien s'il existe
        // un numero double dans chaque ligne ou chaque colonne de la grille
        for (int v = 1; v <= 9; v++) {
            for (int i = 0; i < 9; i++) {
                boolean bx = false;
                boolean by = false;
                for (int j = 0; j < 9; j++) {
                    if (matrix[i][j] == 0) return false;
                    if ((matrix[i][j] == v) && bx) return false;
                    if ((matrix[i][j] == v) && !bx) bx = true;
                    if ((matrix[j][i] == v) && by) return false;
                    if ((matrix[j][i] == v) && !by) by = true;
                }
            }
        }
        // ------
        // Gagne
        return true;
    }
}