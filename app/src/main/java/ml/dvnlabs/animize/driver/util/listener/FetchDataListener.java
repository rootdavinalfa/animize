package ml.dvnlabs.animize.driver.util.listener;

public interface FetchDataListener {
    void onFetchComplete(String data);

    void onFetchFailure(String msg);

    void onFetchStart();
}
