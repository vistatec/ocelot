package com.vistatec.ocelot.config.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "tmManagement")
public class TmManagement {

	private double fuzzyThreshold;
	private int maxResults;
	private List<TmConfig> tms;
	
	public TmManagement() {
		tms = new ArrayList<TmManagement.TmConfig>();
    }

	public void setFuzzyThreshold(double fuzzyThreshold) {
		this.fuzzyThreshold = fuzzyThreshold;
	}

	public double getFuzzyThreshold() {
		return fuzzyThreshold;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setTms(List<TmConfig> tms) {
		this.tms = tms;
	}

	public List<TmConfig> getTms() {
		return tms;
	}

	public void addTm(TmConfig tm) {

		if (tm != null) {
			tms.add(tm);
		}
	}

	@Override
	public String toString() {

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Tm Management: [\n");
		strBuilder.append("Fuzzy Threshold: ");
		strBuilder.append(fuzzyThreshold);
		strBuilder.append("\n");
		strBuilder.append("max Results:  ");
		strBuilder.append(maxResults);
		strBuilder.append("\n");
		if (tms != null) {
			for (TmConfig tmc : tms) {
				strBuilder.append(tmc.toString());
				strBuilder.append("\n");

			}
		}
		strBuilder.append("]");

		return strBuilder.toString();
	}

	public static class TmConfig {

		private String tmName;

		private boolean enabled;

		private String tmDataDir;

		private float penalty;

		private List<TmxFile> tmxFiles;

		public void setTmName(String tmName) {
			this.tmName = tmName;
		}

		public String getTmName() {
			return tmName;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setTmDataDir(String tmDataDir) {
			this.tmDataDir = tmDataDir;
		}

		public String getTmDataDir() {
			return tmDataDir;
		}

		public void setPenalty(float penalty) {
			this.penalty = penalty;
		}

		public float getPenalty() {
			return penalty;
		}

		public void setTmxFiles(List<TmxFile> tmxFiles) {
			this.tmxFiles = tmxFiles;
		}

		public List<TmxFile> getTmxFiles() {
			return tmxFiles;
		}

		@Override
		public String toString() {

			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("TmConfig: [\n");
			strBuilder.append("Tm name: ");
			strBuilder.append(tmName);
			strBuilder.append("\n");
			strBuilder.append("enabled: ");
			strBuilder.append(enabled);
			strBuilder.append("\n");
			strBuilder.append("tm Data Dir: ");
			strBuilder.append(tmDataDir);
			strBuilder.append("\n");
			strBuilder.append("penalty: ");
			strBuilder.append(penalty);
			strBuilder.append("\n");
			if (tmxFiles != null) {
				for (TmxFile f : tmxFiles) {
					strBuilder.append(f.toString());
					strBuilder.append("\n");
				}
			}
			strBuilder.append("]");
			return strBuilder.toString();
		}

		public static class TmxFile {

			private String name;
			
			public TmxFile() {
            }
			
			public TmxFile(String name) {
				
				this.name = name;
            }

			public void setName(String name) {
				this.name = name;
			}

			public String getName() {
				return name;
			}

			@Override
			public String toString() {
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append("TmxFile: [");
				strBuilder.append("name: ");
				strBuilder.append(name);
				strBuilder.append("]");
				return strBuilder.toString();
			}
		}
	}

	
}
