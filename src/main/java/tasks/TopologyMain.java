package tasks;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import bolts.WordCounter;
import bolts.WordNormalizer;
import spouts.WordReader;
public class TopologyMain {
    public static void main(String[] args) throws InterruptedException {
        //定义拓扑
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
        builder.setBolt("word-counter", new WordCounter(),2).fieldsGrouping("word-normalizer", new Fields("word"));

        //配置
        //System.out.println(" args[0] = "+ args[0]);
        Config conf = new Config();

        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        conf.setDebug(false);

        //运行拓扑
        if (args != null && args.length > 0) {
            conf.put("wordsFile", args[1]);
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        }
        else {
            conf.put("wordsFile", args[0]);
            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

            Thread.sleep(20000);

            cluster.shutdown();
        }
    }
}


