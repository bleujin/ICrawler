package net.ion.icrawler.pipeline;

import com.alibaba.fastjson.JSON;

import net.ion.icrawler.Task;
import net.ion.icrawler.model.HasKey;
import net.ion.icrawler.utils.FilePersistentBase;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Store results objects (page models) to files in JSON format.<br>
 * Use model.getKey() as file name if the model implements HasKey.<br>
 * Otherwise use SHA1 as file name.
 * @since 0.2.0
 */
public class JsonFilePageModelPipeline extends FilePersistentBase implements PageModelPipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * new JsonFilePageModelPipeline with default path "./resource/temp"
	 */
	public JsonFilePageModelPipeline() {
		setPath("./resource/temp");
	}

	public JsonFilePageModelPipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(Object o, Task task) {
		String path = this.path + "/" + task.getUUID() + "/";
		try {
			String filename;
			if (o instanceof HasKey) {
				filename = path + ((HasKey) o).key() + ".json";
			} else {
				filename = path + DigestUtils.md5Hex(ToStringBuilder.reflectionToString(o)) + ".json";
			}
			File file = new File(filename);
			if (! file.getParentFile().exists()){
				file.getParentFile().mkdirs() ;
			}
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			printWriter.write(JSON.toJSONString(o));
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}
