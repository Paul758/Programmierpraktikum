import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamingJava {

    public static void main(String[] args) throws IOException {
        Stream<String> lines = StreamingJava.fileLines("data/NaturalGasBilling.csv");
        StreamingJava.NaturalGasBilling.deserialize(NaturalGasBilling.serialize(NaturalGasBilling.orderByInvoiceDateDesc(lines))).forEach(System.out::println);

    }

    // Aufgabe 2) a)
    public static <E> Stream<E> flatStreamOf(List<List<E>> list) {
        return list.stream().flatMap(Collection::stream);
    }

    // Aufgabe 2) b)
    public static <E> Stream<E> mergeStreamsOf(Stream<Stream<E>> stream) {
        return stream.reduce(Stream.empty(), Stream::concat);
    }

    // Aufgabe 2) c)
    public static <E extends Comparable<? super E>> E minOf(List<List<E>> list) throws Exception {
        //Get the minimum
        return flatStreamOf(list).parallel().min(Comparator.naturalOrder()).orElseThrow();

    }

    // Aufgabe 2) d)
    public static <E> E lastWithOf(Stream<E> stream, Predicate<? super E> predicate) throws Exception {
        return stream.filter(predicate).reduce((first, second) -> second).orElseThrow();
    }

    // Aufgabe 2) e)
    public static <E> Set<E> findOfCount(Stream<E> stream, int count) {
        Set<E> countSet = new HashSet<>();

        stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).forEach((key, value) -> {
            if (value == count) {
                countSet.add(key);
            }
        });

        return countSet;
    }

    // Aufgabe 2) f)
    public static IntStream makeStreamOf(String[] strings) {
        return Arrays.stream(strings).flatMapToInt(String::chars);
    }

//-------------------------------------------------------------------------------------------------

    // Aufgabe 3) a)
    public static Stream<String> fileLines(String path) throws IOException {
        try{
            return Files.newBufferedReader(Path.of(path)).lines().skip(1);
        } catch (IOException e){
            System.out.println("File not found");
        }
        return null;
    }

    // Aufgabe 3) b)
    public static double averageCost(Stream<String> lines) {

         return lines
                .flatMap(x -> Stream.of(x.split(",")).skip(12))
                .mapToDouble(Double::parseDouble)
                .average()
                .orElseThrow();

    }

    // Aufgabe 3) c)
    public static long countCleanEnergyLevy(Stream<String> stream) {
        return stream
                .flatMap(x -> Stream.of(x.split(",")).skip(10)).limit(1)
                .filter(x -> x.equals("") || x.equals("0"))
                .count();

    }

    // Aufgabe 3) d)
    record NaturalGasBilling(
            String invoiceDate,
            String fromDate,
            String toDate,
            String billingDays,
            String billedGJ,
            String basicCharge,
            String deliveryCharges,
            String storageAndTransport,
            String commodityCharges,
            String tax,
            String cleanEnergyLevy,
            String carbonTax,
            String amount
    ){
        public static Stream<NaturalGasBilling> orderByInvoiceDateDesc(Stream<String> stream){
            List<List<String>>  recordData = stream.map(x -> Stream.of(x.split(",")).toList()).toList();

            List<NaturalGasBilling> records = new ArrayList<>();

            for(int i = 0; i < recordData.size(); i++){
                    records.add(new NaturalGasBilling(
                            recordData.get(i).get(0),
                            recordData.get(i).get(1),
                            recordData.get(i).get(2),
                            recordData.get(i).get(3),
                            recordData.get(i).get(4),
                            recordData.get(i).get(5),
                            recordData.get(i).get(6),
                            recordData.get(i).get(7),
                            recordData.get(i).get(8),
                            recordData.get(i).get(9),
                            recordData.get(i).get(10),
                            recordData.get(i).get(11),
                            recordData.get(i).get(12)
                    ));
            }

            Stream<NaturalGasBilling> naturalGasBillingStream = records
                    .stream()
                    .sorted(Comparator.comparing(NaturalGasBilling::invoiceDate).reversed());


            return naturalGasBillingStream;
        }


        //Aufgabe 3e)
        Stream<Byte> toBytes() {
            RecordComponent[] rc = NaturalGasBilling.class.getRecordComponents();
            String[] dataFields = new String[13];

            for(int i = 0; i < rc.length; i++){
                try {
                    dataFields[i] = (String) rc[i].getAccessor().invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            List<Byte> bytes = new ArrayList<>();

            for(String string : dataFields){
               char[] charArray = string.toCharArray();
               for(char character : charArray){
                  Byte byteChar = (byte) character;
                  bytes.add(byteChar);
               }
               bytes.add((byte) ',');
            }
            bytes.remove(bytes.size() - 1);
            bytes.add((byte) ';');

            return bytes.stream();
        }

        //Aufgabe 3f)
        public static Stream<Byte> serialize(Stream<NaturalGasBilling> stream){
            String[] attributes = {"Invoice Date", "From Date", "To Date", "Billing Days",
                    "Billed GJ", "Basic charge", "Delivery charges", "Storage and transport",
                    "Commodity charges", "Tax", "Clean energy levy", "Carbon tax", "Amount"};

            List<Byte> bytes = new ArrayList<>();

            for(String string : attributes){
                char[] charArray = string.toCharArray();
                for(char character : charArray){
                    Byte byteChar = (byte) character;
                    bytes.add(byteChar);
                }
                bytes.add((byte) ',');
            }
            bytes.remove(bytes.size() - 1);
            bytes.add((byte) ';');

            Stream<Byte> attributeByteStream = bytes.stream();
            Stream<Byte> flatStreamOfRecords = stream.flatMap(NaturalGasBilling::toBytes);

            return Stream.concat(attributeByteStream, flatStreamOfRecords);
        }


        //Aufgabe 3g)
        public static Stream<NaturalGasBilling> deserialize(Stream<Byte> stream){
            return deserializeBuild(stream).skip(1);
        }

        public static Stream<NaturalGasBilling> deserializeBuild(Stream<Byte> stream){
            List<Byte> byteList =  stream.toList();
            List<Byte> firstRecord = byteList.subList(0, byteList.indexOf((byte) ';'));
            List<Byte> remainingRecords = byteList.subList(byteList.indexOf((byte) ';') + 1, byteList.size());

            var res = Arrays.stream(firstRecord.stream()
                   .map(x -> String.valueOf((char) x.intValue()))
                   .reduce("", String::concat)
                   .split(","))
                   .toList();

            List<NaturalGasBilling> record = new ArrayList<>();
            record.add(createNaturalGasBilling(res));
            Stream<NaturalGasBilling> resStream = record.stream();

            if(!remainingRecords.isEmpty()){
                return Stream.concat(resStream, Objects.requireNonNull(deserializeBuild(remainingRecords.stream())));
            } else {
                return resStream;
            }
        }

        public static NaturalGasBilling createNaturalGasBilling(List<String> data){
                return new NaturalGasBilling(
                        data.get(0),
                        data.get(2),
                        data.get(3),
                        data.get(4),
                        data.get(5),
                        data.get(6),
                        data.get(7),
                        data.get(8),
                        data.get(9),
                        data.get(10),
                        data.get(11),
                        data.get(12),
                        data.get(12)
                );
        }
    }

    // Aufgabe 3h)
    public static Stream<File> findFilesWith(String dir, String startsWith, String endsWith, int maxFiles) {
        List<Path> result;
        Path path = Paths.get(dir);
        try {
            return Files.walk(path)
                     .map(x -> x.toFile())
                     .filter(File::isFile)
                     .filter(f -> (f.getName().startsWith(startsWith) && f.getName().endsWith(endsWith)))
                     .sorted(Comparator.comparing(File::length).reversed())
                     .limit(maxFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("test");
        return null;

    }
}
