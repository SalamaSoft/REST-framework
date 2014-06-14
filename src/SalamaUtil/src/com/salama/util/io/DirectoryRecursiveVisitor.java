package com.salama.util.io;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * @author XingGu Liu
 *
 */
public abstract class DirectoryRecursiveVisitor {
    protected FileFilter _fileFilter = null;
    
	public DirectoryRecursiveVisitor (FileFilter fileFilter) {
		_fileFilter = fileFilter;
    }

    public void recursiveVisit(File curFolder) {
        File[] fileList = curFolder.listFiles();
        if (fileList != null) {
            int foldCount = 0;
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    foldCount++;
                    dealEnterDirectory(fileList[i]);
                    recursiveVisit(fileList[i]);
                    dealLeaveDirectory(fileList[i]);
                } else {
                    if (_fileFilter.accept(fileList[i])) {
                        dealFile(fileList[i]);
                    }
                }
            }
            if (foldCount == 0) {
                dealLeaveLeafDirectory(curFolder);
            }
        }
    }

    protected abstract void dealFile(File currentFile);
    protected abstract void dealEnterDirectory(File currentPath);
    protected abstract void dealLeaveDirectory(File currentPath);
    protected abstract void dealLeaveLeafDirectory(File currentPath);
}
