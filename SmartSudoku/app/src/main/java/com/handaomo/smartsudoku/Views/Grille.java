package com.handaomo.smartsudoku.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.handaomo.smartsudoku.R;

public class Grille extends View {

    final float textSize = 40f;
    private int n, w;

    public int spacing = 5;

    public int gameState = 0; // 0 default state, 1 win, 2 loss

    private Paint paint;   // Pour dessiner la grille (lignes noires)
    private Paint paint1;   // Pour dessiner la grille (lignes noires)
    private Paint paint2;   // Pour le texte des cases fixes
    private Paint paint3;   // Pour dessiner les lignes rouges (grosse)
    private Paint paint4;   // Pour le texte noir des cases a modifier
    private Paint paint5;   // Pour rectangle du fin de jeu

    int[][] matrix = new int[9][9];
    boolean[][] fixIdx = new boolean[9][9];
    String config = "000000000000000000000000000000000000000000000000000000000000000000000000000000000";

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

    public void init() {
        setWillNotDraw(false);
        //Grille de depart
        set(config);

        // Grille Gagnante
        // set("672145398145983672389762451263574819958621743714398526597236184426817935831459267");

        // Grille Perdante
        // set("672145198145983672389762451263574819958621743714398526597236184426817935831459267");

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));

        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setColor(Color.WHITE);

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setTextSize(textSize);
        paint2.setColor(Color.WHITE);

        paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setColor(getResources().getColor(R.color.red));
        paint3.setStrokeWidth(spacing);


        paint4 = new Paint();
        paint4.setAntiAlias(true);
        paint4.setTextSize(textSize);
        paint4.setColor(Color.BLACK);


        paint5 = new Paint();
        paint5.setAntiAlias(true);
        paint5.setStrokeWidth(spacing);
        paint5.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        w = Math.min(screenWidth, screenHeight) - spacing / 2;
        w = w - (w % 9);
        n = w / 9;

        // Dessiner 2 lignes rouges verticales et 2 lignes rouges horizontales
        canvas.drawLine(spacing, n * 3 + (spacing / 2), w, n * 3 + (spacing / 2), paint3);
        canvas.drawLine(spacing, n * 6 + (spacing / 2), w, n * 6 + (spacing / 2), paint3);

        canvas.drawLine(n * 3 + (spacing / 2), spacing, n * 3 + (spacing / 2), w, paint3);
        canvas.drawLine(n * 6 + (spacing / 2), spacing, n * 6 + (spacing / 2), w, paint3);

        for (int i = 0; i < w; i += n) {
            for (int j = 0; j < w; j += n) {
                canvas.drawRect(j + n, i + spacing, j + spacing, i + n,
                        fixIdx[i / n][j / n] ? paint : paint1);
            }
        }

        if(gameState != 0){
            paint5.setColor(getResources().getColor(gameState == 1 ?  R.color.green : R.color.red));
            canvas.drawRect(spacing / 2, spacing / 2, w + spacing / 2, w, paint5);
        }

        String s;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                s = "" + (matrix[j][i] == 0 ? "" : matrix[j][i]);
                if (fixIdx[j][i])
                    canvas.drawText(s, i * n + (n / 2) - (n / 10) + spacing / 2, j * n
                            + (n / 2) + (n / 10) + spacing, paint2);
                else
                    canvas.drawText(s, i * n + (n / 2) - (n / 10) + spacing / 2, j * n
                            + (n / 2) + (n / 10) + spacing, paint4);
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

    public int getElementFromMatrix(int x, int y) {
        return matrix[getYFromMatrix(y)][getXFromMatrix(x)];
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
        matrix[getYFromMatrix(y)][getXFromMatrix(x)] = v;

//        this.invalidate(0, 0, this.getWidth(), this.getHeight());
        this.invalidate();
    }

    public boolean isNotFix(int x, int y) {
        int idy = getYFromMatrix(y);
        int idx = getXFromMatrix(x);
        if (idx > 8 || idy > 8) return true;
        return fixIdx[getYFromMatrix(y)][getXFromMatrix(x)];
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

    public void applyNewConfig(String newConfig) {
        config = newConfig;
        init();
        invalidate();
    }

    public void applyNewConfig(String newConfig, boolean fixIdx[][]) {
        config = newConfig;
        init();
        this.fixIdx = fixIdx;
        invalidate();
    }


    public int[][] getGameMatrix() {
        return matrix;
    }

    public boolean[][] getFixIdx() {
        return fixIdx;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }


    public void setGameState(int gameState) {
        this.gameState = gameState;
        invalidate();
    }
}