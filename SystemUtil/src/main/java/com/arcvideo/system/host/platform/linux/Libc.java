package com.arcvideo.system.host.platform.linux;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

public interface Libc extends Library {

    Libc INSTANCE = (Libc) Native.loadLibrary("c", Libc.class);

    class Sysinfo extends Structure {
        public NativeLong uptime; // Seconds since boot
        // 1, 5, and 15 minute load averages

        public NativeLong[] loads = new NativeLong[3];

        public NativeLong totalram; // Total usable main memory size

        public NativeLong freeram; // Available memory size

        public NativeLong sharedram; // Amount of shared memory

        public NativeLong bufferram; // Memory used by buffers

        public NativeLong totalswap; // Total swap space size

        public NativeLong freeswap; // swap space still available

        public short procs; // Number of current processes

        public NativeLong totalhigh; // Total high memory size

        public NativeLong freehigh; // Available high memory size

        public int mem_unit; // Memory unit size in bytes

        public byte[] _f = new byte[8]; // Won't be written for 64-bit systems

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "uptime", "loads", "totalram", "freeram", "sharedram", "bufferram",
                    "totalswap", "freeswap", "procs", "totalhigh", "freehigh", "mem_unit", "_f" });
        }
    }

    int sysinfo(Sysinfo info);

    /**
     * The getloadavg() function returns the number of processes in the system
     * run queue averaged over various periods of time. Up to nelem samples are
     * retrieved and assigned to successive elements of loadavg[]. The system
     * imposes a maximum of 3 samples, representing averages over the last 1, 5,
     * and 15 minutes, respectively.
     *
     * @param loadavg
     *            array to be filled
     * @param nelem
     *            number of elements in the array to fill
     * @return If the load average was unobtainable, -1 is returned; otherwise,
     *         the number of samples actually retrieved is returned.
     */
    int getloadavg(double[] loadavg, int nelem);

    /**
     * Returns the process ID of the calling process. The ID is guaranteed to be
     * unique and is useful for constructing temporary file names.
     *
     * @return the process ID of the calling process.
     */
    int getpid();

    /**
     * Places the contents of the symbolic link path in the buffer buf, which
     * has size bufsiz.
     *
     * @param path
     *            A symbolic link
     * @param buf
     *            Holds actual path to location pointed to by symlink
     * @param bufsize
     *            size of data in buffer
     * @return readlink() places the contents of the symbolic link path in the
     *         buffer buf, which has size bufsiz. readlink() does not append a
     *         null byte to buf. It will truncate the contents (to a length of
     *         bufsiz characters), in case the buffer is too small to hold all
     *         of the contents.
     */
    int readlink(String path, Pointer buf, int bufsize);

    /**
     * Returns the number of bytes in a memory page, where "page" is a
     * fixed-length block, the unit for memory allocation and file
     * mapping performed by mmap(2).
     *
     * @return the memory page size
     */
    int getpagesize();
}
