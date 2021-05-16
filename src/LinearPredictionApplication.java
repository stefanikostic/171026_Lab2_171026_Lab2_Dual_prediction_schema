import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Result {
    double sentDataPercentage;
    double rootMeanSquareError;

    public Result (double sentDataPercentage, double rootMeanSquareError) {
        this.sentDataPercentage = sentDataPercentage;
        this.rootMeanSquareError = rootMeanSquareError;
    }
}

public class LinearPredictionApplication {

    public static void main (String[] args) throws IOException {
        CreateExcelFile excelFile = new CreateExcelFile();
        List<Double> turbidityData = excelFile.readTurbidityFromExcel("D:\\",
                "beach-water-quality-automated-sensors.xls", "Sheet");
        List<Double> waterTemperaturesData = excelFile.readWaterTemperatureFromExcel("D:\\",
                "beach-water-quality-automated-sensors.xls", "Sheet");

        CreateExcelFile excelFile2D = new CreateExcelFile();
        excelFile.createInitialExcelFile("D:\\Lab2SS-dual-prediction-schema.xls");
        excelFile2D.createInitialExcelFile2D("D:\\Lab2SS-dual-prediction-schema-2d.xls");

        for (int i = 0; i < 15; i++) {
            double errorTurbidity = 0.5 * i; // threshold turbidity of water
            double errorWaterTemperature = 0.2 * i; // threshold water temperature
            Result result = linearPredict(turbidityData, errorTurbidity);
            excelFile.addRowForDualPrediction("Linear Prediction", result.sentDataPercentage,
                    errorTurbidity, result.rootMeanSquareError);
            result = movingAverage(turbidityData, errorTurbidity);
            excelFile.addRowForDualPrediction("Moving Average", result.sentDataPercentage,
                    errorTurbidity, result.rootMeanSquareError);
            result = linearPredictionFirstOrder(turbidityData, errorTurbidity);
            excelFile.addRowForDualPrediction("Linear Prediction First Order", result.sentDataPercentage,
                    errorTurbidity, result.rootMeanSquareError);
            result = linearPredict2D(turbidityData, waterTemperaturesData, errorTurbidity, errorWaterTemperature);
            // algorithms with 2d
            excelFile2D.addRowForDualPrediction2D("Linear Prediction", result.sentDataPercentage,
                    errorTurbidity, errorWaterTemperature, result.rootMeanSquareError);
            result = movingAverage2D(turbidityData, waterTemperaturesData, errorTurbidity, errorWaterTemperature);
            excelFile2D.addRowForDualPrediction2D("Moving Average", result.sentDataPercentage,
                    errorTurbidity, errorWaterTemperature, result.rootMeanSquareError);
            result = linearPredictionFirstOrder2D(turbidityData, waterTemperaturesData, errorTurbidity,
                    errorWaterTemperature);
            excelFile2D.addRowForDualPrediction2D("Linear Prediction First Order", result.sentDataPercentage,
                    errorTurbidity, errorWaterTemperature, result.rootMeanSquareError);
        }

        excelFile.closeFile();
        excelFile2D.closeFile();
    }

    private static Result linearPredict (List<Double> turbidityList, double error) {
        List<Double> sentData = new ArrayList<>();
        sentData.add(turbidityList.get(0));
        double comparingValue = turbidityList.get(0);
        double diff;
        double RMSE = 0;

        for (int i = 1; i < turbidityList.size(); i++) {
            if (i < turbidityList.size() - 1) {
                diff = Math.abs(comparingValue - turbidityList.get(i));
                RMSE += Math.pow(comparingValue - turbidityList.get(i), 2);
                if (diff >= error) {
                    comparingValue = turbidityList.get(i);
                    sentData.add(turbidityList.get(i));
                }
            }
        }

        RMSE = Math.sqrt(RMSE / turbidityList.size());
        double sentDataPercentage = sentData.size() * 100.0 / turbidityList.size();
        Result result = new Result(sentDataPercentage, RMSE);
        return result;
    }

    private static Result linearPredict2D (List<Double> turbidityList, List<Double> waterTemperaturesList,
                                           double errorTurbidity, double errorWaterTemperature) {
        List<Double> sentTurbidityData = new ArrayList<>();
        List<Double> sentWaterTemperatureData = new ArrayList<>();
        sentTurbidityData.add(turbidityList.get(0));
        sentWaterTemperatureData.add(waterTemperaturesList.get(0));
        double comparingValueTurbidity = turbidityList.get(0);
        double comparingValueWaterTemperature = waterTemperaturesList.get(0);

        double diffTurbidity;
        double diffWaterTemperature;
        double RMSE = 0;

        for (int i = 1; i < turbidityList.size(); i++) {
            Double currentTurbidity = turbidityList.get(i);
            Double currentWaterTemperature = waterTemperaturesList.get(i);
            if (i < turbidityList.size() - 1 && i < waterTemperaturesList.size() - 1) {
                diffTurbidity = Math.abs(comparingValueTurbidity - currentTurbidity);
                diffWaterTemperature = Math.abs(comparingValueWaterTemperature - currentWaterTemperature);
                RMSE += Math.pow(diffTurbidity, 2) + Math.pow(diffWaterTemperature, 2);

                if (diffTurbidity >= errorTurbidity && diffWaterTemperature >= errorWaterTemperature) {
                    comparingValueTurbidity = currentTurbidity;
                    comparingValueWaterTemperature = currentWaterTemperature;
                    sentTurbidityData.add(currentTurbidity);
                    sentWaterTemperatureData.add(currentWaterTemperature);
                }
            }
        }

        int size = turbidityList.size() + waterTemperaturesList.size();
        RMSE = Math.sqrt(RMSE / size);
        double sentDataPercentage = (sentTurbidityData.size() + sentWaterTemperatureData.size()) * 100.0 / size;
        Result result = new Result(sentDataPercentage, RMSE);
        return result;
    }

    private static Result movingAverage (List<Double> turbidityList, double error) {
        List<Double> sentData = new ArrayList<>();
        sentData.add(turbidityList.get(0));
        sentData.add(turbidityList.get(1));
        double comparingValue1 = turbidityList.get(0);
        double comparingValue2 = turbidityList.get(1);
        double diff;
        double RMSE = 0;

        for (int i = 2; i < turbidityList.size(); i++) {
            if (i < turbidityList.size() - 1) {
                double temp = (comparingValue1 + comparingValue2) / 2;
                RMSE += Math.pow(temp - turbidityList.get(i), 2);
                diff = Math.abs(temp - turbidityList.get(i));
                if (diff >= error) {
                    comparingValue1 = comparingValue2;
                    comparingValue2 = turbidityList.get(i);
                    sentData.add(turbidityList.get(i));
                }
            }
        }

        RMSE = Math.sqrt(RMSE / turbidityList.size());
        double sentDataPercentage = sentData.size() * 100.0 / turbidityList.size();
        Result result = new Result(sentDataPercentage, RMSE);
        return result;
    }

    private static Result movingAverage2D (List<Double> turbidityList, List<Double> waterTemperaturesList,
                                           double errorTurbidity, double errorWaterTemperature) {
        List<Double> sentTurbidityData = new ArrayList<>();
        double comparingValueTurbidity1 = turbidityList.get(0);
        double comparingValueTurbidity2 = turbidityList.get(1);
        double comparingValueWaterTemperature1 = waterTemperaturesList.get(0);
        double comparingValueWaterTemperature2 = waterTemperaturesList.get(1);

        sentTurbidityData.add(comparingValueTurbidity1);
        sentTurbidityData.add(comparingValueTurbidity2);

        List<Double> sentWaterTemperatureData = new ArrayList<>();
        sentWaterTemperatureData.add(comparingValueWaterTemperature1);
        sentWaterTemperatureData.add(comparingValueWaterTemperature2);

        double diffTurbidity;
        double diffWaterTemperature;
        double RMSE = 0;

        for (int i = 2; i < turbidityList.size(); i++) {
            Double currentTurbidity = turbidityList.get(i);
            Double currentWaterTemperature = waterTemperaturesList.get(i);
            if (i < turbidityList.size() - 1) {
                double tempTurbidity = (comparingValueTurbidity1 + comparingValueTurbidity2) / 2;
                diffTurbidity = Math.abs(tempTurbidity - currentTurbidity);
                double tempWaterTemperature = (comparingValueWaterTemperature1 + comparingValueWaterTemperature2) / 2;
                diffWaterTemperature = Math.abs(tempWaterTemperature - currentWaterTemperature);
                RMSE += Math.pow(diffTurbidity, 2) + Math.pow(diffWaterTemperature, 2);

                if (diffTurbidity >= errorTurbidity && diffWaterTemperature >= errorWaterTemperature) {
                    comparingValueTurbidity1 = comparingValueTurbidity2;
                    comparingValueTurbidity2 = currentTurbidity;
                    comparingValueWaterTemperature1 = comparingValueWaterTemperature2;
                    comparingValueWaterTemperature2 = currentWaterTemperature;
                    sentTurbidityData.add(currentTurbidity);
                    sentWaterTemperatureData.add(currentWaterTemperature);
                }
            }
        }


        int size = turbidityList.size() + waterTemperaturesList.size();
        RMSE = Math.sqrt(RMSE / size);
        double sentDataPercentage = (sentTurbidityData.size() + sentWaterTemperatureData.size()) * 100.0 / size;
        Result result = new Result(sentDataPercentage, RMSE);
        return result;
    }

    public static Result linearPredictionFirstOrder (List<Double> turbidityList, double error) {
        List<Double> sentData = new ArrayList<>();
        sentData.add(turbidityList.get(0));
        double comparingValue1 = turbidityList.get(0);
        double comparingValue2 = turbidityList.get(1);
        double diff;
        double RMSE = 0;

        for (int i = 2; i < turbidityList.size(); i++) {
            if (i < turbidityList.size() - 1) {
                double temp = 2 * comparingValue2 - comparingValue1;
                RMSE += Math.pow(temp - turbidityList.get(i), 2);
                diff = Math.abs(temp - turbidityList.get(i));
                if (diff >= error) {
                    comparingValue2 = comparingValue1;
                    comparingValue1 = turbidityList.get(i);
                    sentData.add(turbidityList.get(i));
                }
            }
        }

        RMSE = Math.sqrt(RMSE / turbidityList.size());
        double sentDataPercentage = sentData.size() * 100.0 / turbidityList.size();
        Result result = new Result(sentDataPercentage, RMSE);
        return result;
    }

    public static Result linearPredictionFirstOrder2D (List<Double> turbidityList, List<Double> waterTemperaturesList,
                                                       double errorTurbidity, double errorWaterTemperature) {
        List<Double> sentTurbidityData = new ArrayList<>();
        double comparingValueTurbidity1 = turbidityList.get(0);
        double comparingValueTurbidity2 = turbidityList.get(1);
        double comparingValueWaterTemperature1 = waterTemperaturesList.get(0);
        double comparingValueWaterTemperature2 = waterTemperaturesList.get(1);

        sentTurbidityData.add(comparingValueTurbidity1);
        sentTurbidityData.add(comparingValueTurbidity2);

        List<Double> sentWaterTemperatureData = new ArrayList<>();
        sentWaterTemperatureData.add(comparingValueWaterTemperature1);
        sentWaterTemperatureData.add(comparingValueWaterTemperature2);

        double diffTurbidity;
        double diffWaterTemperature;
        double RMSE = 0;

        for (int i = 2; i < turbidityList.size(); i++) {
            Double currentTurbidity = turbidityList.get(i);
            Double currentWaterTemperature = waterTemperaturesList.get(i);
            if (i < turbidityList.size() - 1) {
                double tempTurbidity = 2 * comparingValueTurbidity2 - comparingValueTurbidity1;
                diffTurbidity = Math.abs(tempTurbidity - currentTurbidity);
                double tempWaterTemperature = 2 * comparingValueWaterTemperature2 - comparingValueWaterTemperature1;
                diffWaterTemperature = Math.abs(tempWaterTemperature - currentWaterTemperature);

                RMSE += Math.pow(diffTurbidity, 2) + Math.pow(diffWaterTemperature, 2);
                if (diffTurbidity >= errorTurbidity && diffWaterTemperature >= errorWaterTemperature) {
                    comparingValueTurbidity2 = comparingValueTurbidity1;
                    comparingValueTurbidity1 = currentTurbidity;
                    sentTurbidityData.add(currentTurbidity);
                    comparingValueWaterTemperature2 = comparingValueWaterTemperature1;
                    comparingValueWaterTemperature1 = currentWaterTemperature;
                    sentWaterTemperatureData.add(currentWaterTemperature);
                }
            }
        }

        int size = turbidityList.size() + waterTemperaturesList.size();
        RMSE = Math.sqrt(RMSE / size);
        double sentDataPercentage = (sentTurbidityData.size() + sentWaterTemperatureData.size()) * 100.0 / size;
        Result result = new Result(sentDataPercentage, RMSE);
        return result;
    }
}
