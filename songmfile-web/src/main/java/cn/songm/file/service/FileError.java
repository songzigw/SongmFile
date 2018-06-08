/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package cn.songm.file.service;

import cn.songm.common.service.ErrorInfo;

public enum FileError implements ErrorInfo {
    /** 文件超过大小 */
    FIL_OUTSIZE("FIL_101"),
    /** 文件不存在 */
    FIL_NOEXIST("FIL_102"),
	/** 文件格式错误 */
    FIL_FORMAT("FIL_103");

    private final String errCode;
    
    @Override
    public String getErrCode() {
        return errCode;
    }

    private FileError(String errCode) {
        this.errCode = errCode;
    }

    public FileError getInstance(String errCode) {
        for (FileError m : FileError.values()) {
            if (m.getErrCode().equals(errCode)) {
                return m;
            }
        }
        return null;
    }
}
