package com.example.belikov.myapplication.tools;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.belikov.myapplication.R;

import java.util.List;

// Адаптер
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private List<WeatherCard> dataSource;                         // Наш источник данных
    private OnItemClickListener itemClickListener;        // Слушатель, будет устанавливаться извне

    // Этот класс хранит связь между данными и элементами View
    // Сложные данные могут потребовать несколько View на
    // один пункт списка.
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView date;
        public TextView temper;
        public TextView wind;
        public TextView humid;
        public TextView press;

        public ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.card_date);
            temper = v.findViewById(R.id.card_temper);
            wind = v.findViewById(R.id.card_wind);
            humid = v.findViewById(R.id.card_humid);
            press = v.findViewById(R.id.card_press);



            // Обработчик нажатий на этом ViewHolder
//            picture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (itemClickListener != null) {
//                        itemClickListener.onItemClick(v, getAdapterPosition());
//                    }
//                }
//            });
        }
    }

    // Интерфейс для обработки нажатий как в ListView
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    // Сеттер слушателя нажатий
    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    // Передаем в конструктор источник данных
    // В нашем случае это массив, но может быть и запросом к БД
    public WeatherAdapter(List<WeatherCard> dataSource) {
        this.dataSource = dataSource;
    }

    // Создать новый элемент пользовательского интерфейса
    // Запускается менеджером
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // Создаем новый элемент пользовательского интерфейса
        // Через Inflater
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        // Здесь можно установить всякие параметры
        ViewHolder vh = new ViewHolder(v);

        // На каком-то этапе будет переиспользование карточки, и в лог эта строка не попадет
        // а строка onBindViewHolder попадет. Это будет означать, что старая карточка
        // переоткрыта с новыми данными
        Log.d("WeatherAdapter", "onCreateViewHolder");
        return vh;
    }

    // Заменить данные в пользовательском интерфейсе
    // Вызывается менеджером
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Получить элемент из источника данных (БД, интернет...)
        WeatherCard item = dataSource.get(position);
        // Вынести на экран используя ViewHolder
        holder.date.setText(item.getDate());
        holder.temper.setText(item.getTemper());
        holder.wind.setText(item.getWind());
        holder.press.setText(item.getPress());
        holder.humid.setText(item.getHumid());

        // Отрабатывает при необходимости нарисовать карточку
        Log.d("WeatherAdapter", "onBindViewHolder");
    }

    // Вернуть размер данных, вызывается менеджером
    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}