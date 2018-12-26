package com.example.belikov.myapplication;

import android.os.AsyncTask;
import android.util.Log;

// Делатель запросов (класс, умеющий запрашивать страницы)
public class RequestMaker {

    // Слушатель, при помощи которого отправим обратный вызов о готовности страницы
    private OnRequestListener listener;

    // В конструкторе примем слушателя, а в дальнейшем передадим его асинхронной задаче
    public RequestMaker(OnRequestListener onRequestListener){
        listener = onRequestListener;
    }

    // Сделать запрос
    public void make(Integer years) {
        // Создаем объект асинхронной задачи (передаем ей слушатель)
        Requester requester = new Requester(listener);
        // Запускаем асинхронную задачу
        requester.execute(years);
    }

    // Интерфейс слушателя с методами обратного вызова
    public interface OnRequestListener {
        void onStatusProgress(String updateProgress);   // Вызов для обновления прогресса
        void onComplete(Float result);                 // Вызов при завершении обработки
    }

    // AsyncTask - это обертка для выполнения потока в фоне
    // Начальные и конечные методы работают в потоке UI, а основной метод расчета работает в фоне
    private static class Requester extends AsyncTask<Integer, String, Float> {
        private OnRequestListener listener;
        Requester(OnRequestListener listener) {
            this.listener = listener;
        }

        // Обновление прогресса, работает в основном потоке UI
        @Override
        protected void onProgressUpdate(String... strings) {
            listener.onStatusProgress(strings[0]);
        }

        // Выполнить таск в фоновом потоке
        @Override
        protected Float doInBackground(Integer...integers) {
            int days = integers[0] * 365;
            int sum = 0;
            float result;
            for (int i = 0; i < days; i++){
                sum+=(int)(Math.random()*40 -20);
                for(int j = 0; j < 100000; j++){
                    Math.acos((double)j);
                }
            }
            result = (float) sum/days;
            return result;
        }

        // Выдать результат (работает в основном потоке UI)
        @Override
        protected void onPostExecute(Float content) {
            listener.onComplete(content);
        }

    }
}
