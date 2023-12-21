/*
 * Copyright 2023-Now Axel "Azn9" JOLY - contact@azn9.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.azn9.test;

public class Test {

    private int i;
    private Integer z = 23;

    public Integer getI() {
        return this.i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public void readI() {
        System.out.println(this.i);
    }

}
