/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.imap.api.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a range of UID or MSN values.
 */
public class IdRange {

    private long _lowVal;

    private long _highVal;

    public IdRange(long singleVal) {
        _lowVal = singleVal;
        _highVal = singleVal;
    }

    public IdRange(long lowVal, long highVal) {
        if (lowVal > highVal) throw new IllegalArgumentException("LowVal must be <= HighVal");
        _lowVal = lowVal;
        _highVal = highVal;
    }

    public long getLowVal() {
        return _lowVal;
    }

    public long getHighVal() {
        return _highVal;
    }
    
    public void setLowVal(long lowVal) {
        if (lowVal > _highVal) throw new IllegalArgumentException("LowVal must be <= HighVal");
        _lowVal = lowVal;
    }
    
    public void setHighVal(long highVal) {
        if (_lowVal > highVal) throw new IllegalArgumentException("LowVal must be <= HighVal");
        _highVal = highVal;
    }

    /**
     * Return true if the {@link IdRange} includes the given value
     * 
     * @param value
     * @return include
     */
    public boolean includes(long value) {
        return _lowVal <= value && value <= _highVal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (_highVal ^ (_highVal >>> 32));
        result = PRIME * result + (int) (_lowVal ^ (_lowVal >>> 32));
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IdRange other = (IdRange) obj;
        if (_highVal != other._highVal)
            return false;
        if (_lowVal != other._lowVal)
            return false;
        return true;
    }

    /**
     * Renders text suitable for logging.
     * 
     * @return a <code>String</code> representation of this object.
     */
    public String toString() {
        final String retValue = "IdRange ( " + this._lowVal + "->"
                + this._highVal + " )";

        return retValue;
    }
    
    /**
     * Utility method which will copy the given {@link List} and try to merge the
     * {@link IdRange} in the copy before return it.
     * 
     * 
     * @param ranges
     * @return mergedRanges
     */
    public static List<IdRange> mergeRanges(final List<IdRange> ranges) {
        List<IdRange> copy = new ArrayList<IdRange>(ranges);
        Collections.sort(copy, IdRangeComperator.INSTANCE);
       
        for (int i = 0; i < copy.size() -1; i++) {
            IdRange current = copy.get(i);
            IdRange next = copy.get(i +1);
            if (current.getHighVal() >= next.getLowVal() -1) {
                if (next.getHighVal() > current.getHighVal()) {
                    current.setHighVal(next.getHighVal());
                }
                // remove the merged id range and decrease the count
                copy.remove(next);
                i--;
            }
        }
        return copy;

    }

    private static class IdRangeComperator implements Comparator<IdRange> {

        private static IdRangeComperator INSTANCE = new IdRangeComperator();

        public int compare(IdRange range1, IdRange range2) {
            return (int) (range1.getLowVal() - range2.getLowVal());
        }

    }

}
