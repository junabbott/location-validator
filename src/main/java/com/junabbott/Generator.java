package com.junabbott;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

/**
 * Created by junabbott on 7/10/17.
 */
public class Generator {

  public static void main(String[] args) {
    final Config conf = ConfigFactory.load();
    final String actorSystem = conf.getString("generator.actorSystem");
    final int rows = conf.getInt("generator.rows");
    String filename = "";
    if (args.length == 1) {
      filename = args[0];
    } else {
      filename = conf.getString("generator.filename");
    }
    final long start = System.currentTimeMillis();
    final ActorSystem system = ActorSystem.create(actorSystem);
    final Materializer materializer = ActorMaterializer.create(system);
    final Utils utils = new Utils();
    final Source<Integer, NotUsed> source = Source.range(1, rows); // default to 1 million rows
    final CompletionStage<IOResult> result = source
        .map(num -> ByteString.fromString(utils.getRandomRow()))
        .runWith(FileIO.toPath(Paths.get(filename)), materializer);
    result
        .thenRun(() -> System.out.println("Completed in " + (System.currentTimeMillis() - start) + " ms"))
        .thenRun(() -> system.terminate());
  }
}
