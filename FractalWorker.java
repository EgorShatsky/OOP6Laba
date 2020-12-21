import javax.swing.*;

public class FractalWorker extends SwingWorker {                    //принимает object, object
    @Override                                                       //Переопрделение
    protected Object doInBackground() throws Exception {             //Метод doInBackground() должен возвращать объект типа Object, так как это указано в объявлении SwingWorker <T, V>. Просто верните null.
        return null;
    }
}