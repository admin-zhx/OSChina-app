package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class CodeTree extends Entity {
	
	public final static String TYPE_TREE = "tree";
	public final static String TYPE_BLOB = "blob";
	
	@JsonProperty("name")
	private String _name;
	
	@JsonProperty("type")
	private String _type;
	
	@JsonProperty("mode")
	private String _mode;
	
	private String _path;
	
	public String getPath() {
		return _path;
	}
	public void setPath(String path) {
		this._path = path;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public String getType() {
		return _type;
	}
	public void setType(String type) {
		this._type = type;
	}
	public String getMode() {
		return _mode;
	}
	public void setMode(String mode) {
		this._mode = mode;
	}


    // 判断是不是代码文件
    public boolean isCodeTextFile(String fileName) {
        boolean res = false;
        // 文件的后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index);
        }
        String codeFileSuffix[] = new String[]
                {
                        ".java",
                        ".confg",
                        ".ini",
                        ".xml",
                        ".json",
                        ".txt",
                        ".go",
                        ".php",
                        ".php3",
                        ".php4",
                        ".php5",
                        ".js",
                        ".css",
                        ".html",
                        ".properties",
                        ".c",
                        ".hpp",
                        ".h",
                        ".hh",
                        ".cpp",
                        ".cfg",
                        ".rb",
                        ".example",
                        ".gitignore",
                        ".project",
                        ".classpath",
                        ".m",
                        ".md",
                        ".rst",
                        ".vm",
                        ".cl",
                        ".py",
                        ".pl",
                        ".haml",
                        ".erb",
                        ".scss",
                        ".bat",
                        ".coffee",
                        ".as",
                        ".sh",
                        ".m",
                        ".pas",
                        ".cs",
                        ".groovy",
                        ".scala",
                        ".sql",
                        ".bas",
                        ".xml",
                        ".vb",
                        ".xsl",
                        ".swift",
                        ".ftl",
                        ".yml",
                        ".ru",
                        ".jsp",
                        ".markdown",
                        ".cshap",
                        ".apsx",
                        ".sass",
                        ".less",
                        ".ftl",
                        ".haml",
                        ".log",
                        ".tx",
                        ".csproj",
                        ".sln",
                        ".clj",
                        ".scm",
                        ".xhml",
                        ".xaml",
                        ".lua"
                };
        for (String string : codeFileSuffix) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }

        // 特殊的文件
        String fileNames[] = new String[]
                {
                        "LICENSE", "TODO", "README", "readme", "makefile", "gemfile", "gemfile.*", "gemfile.lock", "CHANGELOG"
                };

        for (String string : fileNames) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }

        return res;
    }

    // 判断是否是图片
    public boolean isImage(String fileName) {
        boolean res = false;
        // 图片后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index);
        }
        String imageSuffix[] = new String[]
                {
                        ".png", ".jpg", ".jpeg", ".jpe", ".bmp", ".exif", ".dxf", ".wbmp", ".ico", ".jpe", ".gif", ".pcx", ".fpx", ".ufo", ".tiff", ".svg", ".eps", ".ai", ".tga", ".pcd", ".hdri"
                };
        for (String string : imageSuffix) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }
        return res;
    }
}
