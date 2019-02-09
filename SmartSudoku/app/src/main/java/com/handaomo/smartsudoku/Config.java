package com.handaomo.smartsudoku;

public interface Config {
//    String baseURL = "https://smart-sudoku.herokuapp.com/";
    String baseURL = "http://192.168.43.28:5000/";
    String registerURL = baseURL + "register";
    String apiBaseURL = baseURL + "api/";

    // Socket-io events
    String EVENT_NEW_GRID = "new_grid";
    String EVENT_NEW_GRID_UPDATE = "new_grid_update";
    String EMIT_SOCKET_CHECK_UPDATES = "check_new_grid";

    // Intent Result code
    int LOGIN_RESULT = 25;
    int NOTIFICATION_RESULT = 26;
}
