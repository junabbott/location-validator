package com.junabbott;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by junabbott on 7/12/17.
 */
public class Validator {

  public static double avgPtsPerMaid(ConcurrentHashMap<String, Integer> AAIDs, ConcurrentHashMap<String, Integer> IDFAs) {
    int sum = 0;
    for(Map.Entry<String, Integer> entry: AAIDs.entrySet()) {
      sum += entry.getValue();
    }
    for(Map.Entry<String, Integer> entry: IDFAs.entrySet()) {
      sum += entry.getValue();
    }
    return sum / (AAIDs.size() + IDFAs.size());
  }

  public static void main(String[] args) {
    final Config conf = ConfigFactory.load();
    final String actorSystem = conf.getString("validator.actorSystem");
    String inputFileName = "";
    String outputFileName = "";
    if (args.length == 2) {
      inputFileName = args[0];
      outputFileName = args[1];
    } else {
      inputFileName = conf.getString("validator.input.filename");
      outputFileName = conf.getString("validator.output.filename");
    }

    if (Files.exists(Paths.get(inputFileName))) {
      final long start = System.currentTimeMillis();
      final ActorSystem system = ActorSystem.create(actorSystem);
      final Materializer materializer = ActorMaterializer.create(system);
      final AtomicInteger rowCounts = new AtomicInteger();
      final ConcurrentHashMap<String, Integer> AAIDs = new ConcurrentHashMap<>();
      final ConcurrentHashMap<String, Integer> IDFAs = new ConcurrentHashMap<>();
      final AtomicInteger validCount = new AtomicInteger();
      final AtomicInteger invalidCount = new AtomicInteger();
      final AtomicInteger rowIsNullCount = new AtomicInteger();
      final AtomicInteger invalidColCount = new AtomicInteger();
      final AtomicInteger invalidMaidCount = new AtomicInteger();
      final AtomicInteger invalidTimestampCount = new AtomicInteger();
      final AtomicInteger invalidLatitudeCount = new AtomicInteger();
      final AtomicInteger invalidLongitudeCount = new AtomicInteger();
      final AtomicInteger invalidLocMethodCount = new AtomicInteger();
      final AtomicInteger invalidLocModeCount = new AtomicInteger();
      final AtomicInteger invalidIpCount = new AtomicInteger();
      final AtomicInteger invalidHorAccCount = new AtomicInteger();
      final AtomicInteger invalidCountryCodeCount = new AtomicInteger();
      final CompletionStage<IOResult> result = FileIO.fromPath(Paths.get(inputFileName))
          .via(Framing.delimiter(ByteString.fromString("\n"), 300, FramingTruncation.DISALLOW))
          .map(data ->
          {
            ValidationResult res = Validation.isValidData(data.utf8String().trim());
            rowCounts.getAndIncrement();
            if (res.isValid()) {
              validCount.getAndIncrement();
              if (res.isIDFA()) {
                if (IDFAs.containsKey(res.getMaid())) {
                  IDFAs.put(res.getMaid(), IDFAs.get(res.getMaid()) + 1);
                } else {
                  IDFAs.put(res.getMaid(), 1);
                }
              } else {
                if (AAIDs.containsKey(res.getMaid())) {
                  AAIDs.put(res.getMaid(), AAIDs.get(res.getMaid()) + 1);
                } else {
                  AAIDs.put(res.getMaid(), 1);
                }
              }
            } else {
              invalidCount.getAndIncrement();
              ValidationResult.InvalidReason[] reasons = res.getInvalidReasons();
              for (int i = 0; i < reasons.length; i++) {
                if (reasons[i] == ValidationResult.InvalidReason.ROW_IS_NULL) {
                  rowIsNullCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_COL_COUNT) {
                  invalidColCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_MAID) {
                  invalidMaidCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_TIMESTAMP) {
                  invalidTimestampCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_LATITUDE) {
                  invalidLatitudeCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_LONGITUDE) {
                  invalidLongitudeCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_LOC_METHOD) {
                  invalidLocMethodCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_LOC_MODE) {
                  invalidLocModeCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_IP) {
                  invalidIpCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_HOR_ACC) {
                  invalidHorAccCount.getAndIncrement();
                }
                if (reasons[i] == ValidationResult.InvalidReason.INVALID_COUNTRY_CODE) {
                  invalidCountryCodeCount.getAndIncrement();
                }
              }
            }
            return res;
          })
          .filter(res -> res.isValid())
          .map(res -> ByteString.fromString(res.getRow() + "\n"))
          .runWith(FileIO.toPath(Paths.get(outputFileName)), materializer);

      result
          .thenRun(() -> {
            long end = System.currentTimeMillis();
            System.out.println("Count of unique MAIDs " + (AAIDs.size() + IDFAs.size()) + " Androids " + AAIDs.size() + " Apple/iOS " + IDFAs.size());
            System.out.println("Count of total rows/location pts: " + rowCounts);
            System.out.println("Count of valid rows/location pts: " + validCount);
            System.out.println("Count of invalid rows/locations pts: " + invalidCount);
            System.out.println("Average pts per maid "+ avgPtsPerMaid(AAIDs, IDFAs));
            System.out.println("Count of null rows: " + rowIsNullCount);
            System.out.println("Count of invalid column count: " + invalidColCount);
            System.out.println("Count of invalid maid: " + invalidMaidCount);
            System.out.println("Count of invalid timestamp: " + invalidTimestampCount);
            System.out.println("Count of invalid latitude: " + invalidLatitudeCount);
            System.out.println("Count of invalid longitude: " + invalidLongitudeCount);
            System.out.println("Count of invalid Location Method: " + invalidLocMethodCount);
            System.out.println("Count of invalid location Mode: " + invalidLocModeCount);
            System.out.println("Count of invalid ip address: " + invalidIpCount);
            System.out.println("Count of invalid horizontal accuracy: " + invalidHorAccCount);
            System.out.println("Count of invalid country code: " + invalidCountryCodeCount);
            System.out.println("Completed in " + (end - start) + " ms");
          })
          .thenRun(() -> system.terminate());
    } else {
      System.out.println("NO FILE FOUND");
      System.exit(1);
    }
  }
}
