package support;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by lin on 2018/10/5.
 * 该工具只要是为了获取某个包下的所有类文件
 */
public class ClassUtils {

    /**
     * 获取某个包下的所有类
     *
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            System.out.println(packageName);
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagerPath = url.getPath().replace("%20", "");
                        addClass(classSet, packagerPath, packageName);
                        //进一步操作
                    } else if (protocol.equals("jar")) {
                        //跳过启动idea时自身需要加入的jar
                        if (url.getPath().startsWith("file:/F:/IntelliJ")) {
                            continue;
                        }
                        //jar引入我们不做任何处理
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                                .replaceAll("/", ".");
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return classSet;


    }

    /**
     * 将指定包路径下的类添加到集合当中
     *
     * @param classSet
     * @param packagePath 父路径
     * @param packageName 所在包名
     */
    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) ||
                        file.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) { //文件
                String className = fileName.substring(0, fileName.
                        lastIndexOf("."));
                if (packageName.trim() != null) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else { //目录
                String subPackagePath = fileName;
                if (packagePath.trim() != null) {
                    subPackagePath = packagePath + "/" + subPackagePath; //更新路径名
                }
                String subPackageName = fileName;
                if (packageName.trim() != null) { //跟新所在包名
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        }


    }

    /**
     * 将类添加到集合当中
     *
     * @param classSet
     * @param className 类的全路径名
     */
    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }

    /**
     * 获取类加载器
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     *
     * @param className
     * @param isInitialized
     * @return
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return cls;

    }


}
