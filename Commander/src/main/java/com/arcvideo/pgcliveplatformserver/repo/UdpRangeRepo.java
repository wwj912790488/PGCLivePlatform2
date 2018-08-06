package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.UdpRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by slw on 2018/6/22.
 */
public interface UdpRangeRepo extends JpaRepository<UdpRange, Long>, JpaSpecificationExecutor<UdpRange> {
}
