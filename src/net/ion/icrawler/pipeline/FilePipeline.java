package net.ion.icrawler.pipeline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;
import net.ion.icrawler.utils.FilePersistentBase;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store results in files.<br>
 */
// @ThreadSafe
public class FilePipeline extends FilePersistentBase implements Pipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public FilePipeline() {
		setPath("./resource/temp/");
	}

	public FilePipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path + DigestUtils.md5Hex(resultItems.getRequest().getUrl()) + ".html")), "UTF-8"));
			printWriter.println("url:\t" + resultItems.getRequest().getUrl());
			for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
				if (entry.getValue() instanceof Iterable) {
					Iterable value = (Iterable) entry.getValue();
					printWriter.println(entry.getKey() + ":");
					for (Object o : value) {
						printWriter.println(o);
					}
				} else {
					printWriter.println(entry.getKey() + ":\t" + entry.getValue());
				}
			}
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}
